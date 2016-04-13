package org.sosy_lab.cpachecker.cpa.formulaslicing;

import static org.sosy_lab.solver.api.SolverContext.ProverOptions.GENERATE_UNSAT_CORE;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.SingletonPrecision;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustmentResult;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustmentResult.Action;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;
import org.sosy_lab.cpachecker.cpa.loopstack.LoopstackState;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.LiveVariables;
import org.sosy_lab.cpachecker.util.LoopStructure;
import org.sosy_lab.cpachecker.util.Pair;
import org.sosy_lab.cpachecker.util.predicates.pathformula.PathFormula;
import org.sosy_lab.cpachecker.util.predicates.pathformula.PathFormulaManager;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.Solver;
import org.sosy_lab.solver.SolverException;
import org.sosy_lab.solver.api.BooleanFormula;
import org.sosy_lab.solver.api.BooleanFormulaManager;
import org.sosy_lab.solver.api.Formula;
import org.sosy_lab.solver.api.FunctionDeclaration;
import org.sosy_lab.solver.api.FunctionDeclarationKind;
import org.sosy_lab.solver.api.ProverEnvironment;
import org.sosy_lab.solver.visitors.DefaultFormulaVisitor;
import org.sosy_lab.solver.visitors.TraversalProcess;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Options(prefix="cpa.slicing")
public class FormulaSlicingManager implements IFormulaSlicingManager {
  private final PathFormulaManager pfmgr;
  private final BooleanFormulaManager bfmgr;
  private final FormulaManagerView fmgr;
  private final InductiveWeakeningManager inductiveWeakeningManager;
  private final Solver solver;
  private final FormulaSlicingStatistics statistics;
  private final RCNFManager rcnfManager;
  private final LiveVariables liveVariables;
  private final LoopStructure loopStructure;

  /**
   * For each node, map a set of constraints to whether it is unsatisfiable
   * or satisfiable (maps to |true| <=> |UNSAT|).
   * If a set of constraints is satisfiable, any subset of it is also
   * satisfiable.
   * If a set of constraints is unsatisfiable, any superset of it is also
   * unsatisfiable.
   */
  private final Map<CFANode, Map<Set<BooleanFormula>, Boolean>> unsatCache;

  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  private final LogManager logger;

  @Option(secure=true, description="Check target states reachability")
  private boolean checkTargetStates = true;

  @Option(secure=true, description="Filter lemmas by liveness")
  private boolean filterByLiveness = true;

  FormulaSlicingManager(
      Configuration config,
      PathFormulaManager pPfmgr,
      FormulaManagerView pFmgr,
      CFA pCfa,
      InductiveWeakeningManager pInductiveWeakeningManager,
      RCNFManager pRcnfManager,
      Solver pSolver,
      LogManager pLogger)
      throws InvalidConfigurationException {
    logger = pLogger;
    config.inject(this);
    fmgr = pFmgr;
    pfmgr = pPfmgr;
    inductiveWeakeningManager = pInductiveWeakeningManager;
    solver = pSolver;
    bfmgr = pFmgr.getBooleanFormulaManager();
    rcnfManager = pRcnfManager;
    statistics = new FormulaSlicingStatistics();
    unsatCache = new HashMap<>();
    Preconditions.checkState(pCfa.getLiveVariables().isPresent() &&
      pCfa.getLoopStructure().isPresent());
    liveVariables = pCfa.getLiveVariables().get();
    loopStructure = pCfa.getLoopStructure().get();
  }

  @Override
  public Collection<? extends SlicingState> getAbstractSuccessors(
      SlicingState oldState, CFAEdge edge)
      throws CPATransferException, InterruptedException {

    statistics.propagation.start();
    SlicingIntermediateState iOldState;

    if (oldState.isAbstracted()) {
      iOldState = abstractStateToIntermediate(oldState.asAbstracted());
    } else {
      iOldState = oldState.asIntermediate();
    }

    PathFormula outPath = pfmgr.makeAnd(iOldState.getPathFormula(), edge);
    SlicingIntermediateState out = SlicingIntermediateState.of(
        edge.getSuccessor(), outPath, iOldState.getAbstractParent());
    statistics.propagation.stop();

    return Collections.singleton(out);
  }

  @Override
  public Optional<PrecisionAdjustmentResult> prec(
      SlicingState pState,
      UnmodifiableReachedSet pStates, AbstractState pFullState)
      throws CPAException, InterruptedException
  {
    SlicingIntermediateState iState;
    if (pState.isAbstracted()) {

      // We do not use the other invariant => do not repeat the computation.
      return Optional.of(
          PrecisionAdjustmentResult.create(
              pState, SingletonPrecision.getInstance(), Action.CONTINUE)
      );
    } else {
      iState = pState.asIntermediate();
    }

    boolean hasTargetState = Iterables.filter(
        AbstractStates.asIterable(pFullState),
        AbstractStates.IS_TARGET_STATE).iterator().hasNext();
    boolean shouldPerformAbstraction = shouldPerformAbstraction(
        iState.getNode(), pFullState);
    if (hasTargetState && checkTargetStates && isUnreachable(iState)) {
      return Optional.absent();
    }

    if (shouldPerformAbstraction) {
      statistics.abstractionLocations++;

      Optional<SlicingAbstractedState> oldState = findOldToMerge(
          pStates, pFullState, pState);

      SlicingAbstractedState out;
      if (oldState.isPresent()) {
        // Perform slicing, there is a relevant "to-merge" element.
        // Delay checking reachability.
        Optional<SlicingAbstractedState> slicingOut =
            performSlicing(iState, oldState.get());
        if (slicingOut.isPresent()) {
          out = slicingOut.get();
        } else {
          return Optional.absent();
        }
      } else {
        if (isUnreachable(iState)) {
          return Optional.absent();
        }
        out = SlicingAbstractedState.ofClauses(
            toRcnf(iState),
            iState.getPathFormula().getSsa(),
            iState.getPathFormula().getPointerTargetSet(),
            fmgr,
            iState.getNode(),
            Optional.of(iState)
        );
      }

      return Optional.of(
          PrecisionAdjustmentResult.create(
              out, SingletonPrecision.getInstance(), Action.CONTINUE)
      );
    } else {
      return Optional.of(PrecisionAdjustmentResult.create(
          pState, SingletonPrecision.getInstance(), Action.CONTINUE));
    }
  }

  /**
   * Convert the input state to the set of instantiated lemmas in RCNF.
   */
  private Set<BooleanFormula> toRcnf(SlicingIntermediateState iState)
      throws InterruptedException {
    PathFormula pf = iState.getPathFormula();
    final SSAMap ssa = pf.getSsa();
    CFANode node = iState.getNode();
    SlicingAbstractedState abstractParent = iState.getAbstractParent();

    BooleanFormula transition = bfmgr.and(
        fmgr.simplify(pf.getFormula()),
        bfmgr.and(abstractParent.getInstantiatedAbstraction())
    );

    // Filter non-final UFs out first, as they can not be quantified.
    transition = fmgr.filterLiterals(transition,
        new Predicate<BooleanFormula>() {
          @Override
          public boolean apply(BooleanFormula input) {
            return !hasDeadUf(input, ssa);
          }
        });
    BooleanFormula quantified = fmgr.quantifyDeadVariables(
        transition, ssa);

    Set<BooleanFormula> lemmas = rcnfManager.toLemmas(quantified);

    Set<BooleanFormula> finalLemmas = new HashSet<>();
    for (BooleanFormula lemma : lemmas) {
      if (filterByLiveness &&
          Sets.intersection(
              ImmutableSet.copyOf(
                  liveVariables.getLiveVariableNamesForNode(node).filter(
                      Predicates.<String>notNull())),
              fmgr.extractFunctionNames(fmgr.uninstantiate(lemma))).isEmpty()
          ) {

        continue;
      }
      finalLemmas.add(fmgr.uninstantiate(lemma));
    }
    return finalLemmas;
  }


  private final Map<Pair<SlicingIntermediateState, SlicingAbstractedState>,
      SlicingAbstractedState> slicingCache = new HashMap<>();

  private Optional<SlicingAbstractedState> performSlicing(
      final SlicingIntermediateState iState,
      final SlicingAbstractedState oldState
  ) throws CPAException, InterruptedException {
    SlicingAbstractedState out = slicingCache.get(Pair.of(iState, oldState));
    if (out != null) {
      return Optional.of(out);
    }

    final SlicingAbstractedState fromState = iState.getAbstractParent();

    Set<BooleanFormula> candidateLemmas = Sets.filter(
        fromState.getAbstraction(),
        new Predicate<BooleanFormula>() {
          @Override
          public boolean apply(BooleanFormula input) {
            return fromState.getAbstraction().contains(input) &&
                allVarsInSSAMap(input,
                    oldState.getSSA(),
                    iState.getPathFormula().getSsa());
          }
        });

    PathFormulaWithStartSSA path =
        new PathFormulaWithStartSSA(iState.getPathFormula(), iState
            .getAbstractParent().getSSA());
    if (oldState == fromState) {

      // TODO: optimization can be extended to nested loops
      // as well.
      if (fromState.getInductiveUnder().contains(path)) {

        // Optimization for non-nested loops.
        return Optional.of(SlicingAbstractedState.copyOf(fromState));
      }
    }

    Set<BooleanFormula> finalClauses;
    Set<PathFormulaWithStartSSA> inductiveUnder;
    if (isUnreachable(iState)) {
      return Optional.absent();
    }
    try {
      statistics.inductiveWeakening.start();
      if (fromState != oldState) {
        finalClauses = inductiveWeakeningManager.findInductiveWeakeningForRCNF(
            fromState.getSSA(),
            fromState.getAbstraction(),
            iState.getPathFormula(),
            candidateLemmas
        );
        inductiveUnder = ImmutableSet.of();
      } else {

        // No nested loops: remove lemmas on both sides.
        finalClauses = inductiveWeakeningManager.findInductiveWeakeningForRCNF(
            fromState.getSSA(),
            iState.getPathFormula(),
            candidateLemmas
        );
        inductiveUnder = Sets.union(
            fromState.getInductiveUnder(), ImmutableSet.of(path)
        );
      }
    } catch (SolverException pE) {
      throw new CPAException("Solver call failed", pE);
    } finally {
      statistics.inductiveWeakening.stop();
    }

    out = SlicingAbstractedState.makeSliced(
        finalClauses,
        // It is crucial to use the previous SSA so that PathFormulas stay
        // the same and can be cached.
        oldState.getSSA(),
        iState.getPathFormula().getPointerTargetSet(),
        fmgr,
        iState.getNode(),
        Optional.of(iState),
        inductiveUnder
    );
    slicingCache.put(Pair.of(iState, oldState), out);
    return Optional.of(out);
  }



  private boolean isUnreachable(SlicingIntermediateState iState)
      throws InterruptedException, CPAException {

    BooleanFormula prevSlice = bfmgr.and(iState.getAbstractParent().getAbstraction());
    BooleanFormula instantiatedFormula =
        fmgr.instantiate(prevSlice, iState.getAbstractParent().getSSA());
    BooleanFormula reachabilityQuery = bfmgr.and(
        // TODO: apply factorization to formulas.
        iState.getPathFormula().getFormula(), instantiatedFormula);

    Set<BooleanFormula> constraints = ImmutableSet.copyOf(
        bfmgr.toConjunctionArgs(reachabilityQuery, true));

    CFANode node = iState.getNode();
    statistics.satChecksLocations.add(node);

    Map<Set<BooleanFormula>, Boolean> stored = unsatCache.get(node);
    if (stored != null) {
      for (Entry<Set<BooleanFormula>, Boolean> isUnsatResults : stored
          .entrySet()) {
        Set<BooleanFormula> cachedConstraints = isUnsatResults.getKey();
        Boolean cachedIsUnsat = isUnsatResults.getValue();

        if (cachedIsUnsat && constraints.containsAll(cachedConstraints)) {
          statistics.cachedSatChecks++;
          return true;
        } else if (!cachedIsUnsat &&
            cachedConstraints.containsAll(constraints)) {
          statistics.cachedSatChecks++;
          return false;
        }
      }
    }

    if (stored == null) {
      stored = new HashMap<>();
    } else {
      stored = new HashMap<>(stored);
    }

    statistics.reachabilityCheck.start();
    try (ProverEnvironment pe = solver.newProverEnvironment(GENERATE_UNSAT_CORE)){
      for (BooleanFormula f : constraints) {
        pe.addConstraint(f);
      }
      if (pe.isUnsat()) {
        Set<BooleanFormula> unsatCore = ImmutableSet.copyOf(pe.getUnsatCore());
        stored.put(unsatCore, true);
        return true;
      } else {
        stored.put(constraints, false);
        return false;
      }
    } catch (SolverException pE) {
      throw new CPAException("Solver exception suppressed: ", pE);
    } finally {
      unsatCache.put(node, ImmutableMap.copyOf(stored));
      statistics.reachabilityCheck.stop();
    }
  }

  @Override
  public SlicingState getInitialState(CFANode node) {
    return SlicingAbstractedState.empty(fmgr, node);
  }

  @Override
  public boolean isLessOrEqual(SlicingState pState1, SlicingState pState2) {
    Preconditions.checkState(pState1.isAbstracted() == pState2.isAbstracted());

    if (pState1.isAbstracted()) {
      return isLessOrEqualAbstracted(pState1.asAbstracted(), pState2.asAbstracted());
    } else {
      return isLessOrEqualIntermediate(pState1.asIntermediate(), pState2.asIntermediate());
    }
  }

  private boolean isLessOrEqualIntermediate(
      SlicingIntermediateState pState1,
      SlicingIntermediateState pState2) {
    SlicingIntermediateState iState1 = pState1.asIntermediate();
    SlicingIntermediateState iState2 = pState2.asIntermediate();
    return (iState1.isMergedInto(iState2) ||
        iState1.getPathFormula().getFormula().equals(iState2.getPathFormula().getFormula()))
        && isLessOrEqualAbstracted(iState1.getAbstractParent(), iState2.getAbstractParent());
  }

  private boolean isLessOrEqualAbstracted(
      SlicingAbstractedState pState1,
      SlicingAbstractedState pState2
  ) {
    // More clauses => more constraints => the state is *smaller*.
    return pState1.getAbstraction().containsAll(pState2.getAbstraction());
  }

  private SlicingIntermediateState joinIntermediateStates(
      SlicingIntermediateState newState,
      SlicingIntermediateState oldState) throws InterruptedException {

    if (!newState.getAbstractParent().equals(oldState.getAbstractParent())) {

      // No merge.
      return oldState;
    }

    if (newState.isMergedInto(oldState)) {
      return oldState;
    } else if (oldState.isMergedInto(newState)) {
      return newState;
    }

    if (oldState.getPathFormula().equals(newState.getPathFormula())) {
      return newState;
    }
    PathFormula mergedPath = pfmgr.makeOr(newState.getPathFormula(),
        oldState.getPathFormula());

    SlicingIntermediateState out = SlicingIntermediateState.of(
        oldState.getNode(), mergedPath, oldState.getAbstractParent()
    );
    newState.setMergedInto(out);
    oldState.setMergedInto(out);
    return out;
  }

  private SlicingIntermediateState abstractStateToIntermediate(
      SlicingAbstractedState pSlicingAbstractedState) {
    return SlicingIntermediateState.of(
        pSlicingAbstractedState.getNode(),
        new PathFormula(
            bfmgr.makeBoolean(true),
            pSlicingAbstractedState.getSSA(),
            pSlicingAbstractedState.getPointerTargetSet(),
            0), pSlicingAbstractedState);
  }

  private boolean shouldPerformAbstraction(CFANode node, AbstractState pFullState) {

    LoopstackState loopState = AbstractStates.extractStateByType(pFullState,
        LoopstackState.class);
    Preconditions.checkState(loopState != null, "LoopstackCPA must be enabled for formula slicing"
        + " to work.");

    // Slicing is only performed on the loop heads.
    return loopStructure.getAllLoopHeads().contains(node)
        && loopState.isLoopCounterAbstracted();
  }

  @Override
  public SlicingState merge(SlicingState pState1, SlicingState pState2) throws InterruptedException {
    Preconditions.checkState(pState1.isAbstracted() == pState2.isAbstracted());

    if (pState1.isAbstracted()) {

      // No merge.
      return pState2;
    } else {
      SlicingIntermediateState iState1 = pState1.asIntermediate();
      SlicingIntermediateState iState2 = pState2.asIntermediate();
      return joinIntermediateStates(iState1, iState2);
    }
  }

  /**
   * If the variable got removed from SSAMap along the path, it should not be
   * in the set of candidate lemmas anymore, as one version would be
   * instantiated and another version would not.
   */
  private boolean allVarsInSSAMap(
      BooleanFormula lemma,
      SSAMap oldSsa,
      SSAMap newSsa) {
    for (String var : fmgr.extractVariableNames(lemma)) {
      if (oldSsa.containsVariable(var) != newSsa.containsVariable(var)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find a previous closest occurrence in ARG in the same partition, or
   * {@code Optional.absent()}
   */
  private Optional<SlicingAbstractedState> findOldToMerge
  (UnmodifiableReachedSet states, AbstractState pArgState, SlicingState state) {
    Set<SlicingAbstractedState> filteredSiblings =
        ImmutableSet.copyOf(
            AbstractStates.projectToType(
                states.getReached(pArgState),
                SlicingAbstractedState.class)
        );
    if (filteredSiblings.isEmpty()) {
      return Optional.absent();
    }

    // We follow the chain of backpointers until we intersect something in the
    // same partition.
    // The chain is necessary as we might have nested loops.
    SlicingState a = state;
    while (true) {
      if (a.isAbstracted()) {
        SlicingAbstractedState aState = a.asAbstracted();

        if (filteredSiblings.contains(aState)) {
          return Optional.of(aState);
        } else {
          if (!aState.getGeneratingState().isPresent()) {
            // Empty.
            return Optional.absent();
          }
          a = aState.getGeneratingState().get().getAbstractParent();
        }
      } else {
        SlicingIntermediateState iState = a.asIntermediate();
        a = iState.getAbstractParent();
      }
    }
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    pStatsCollection.add(statistics);
  }

  private boolean hasDeadUf(BooleanFormula atom, final SSAMap pSSAMap) {
    final AtomicBoolean out = new AtomicBoolean(false);
    fmgr.visitRecursively(new DefaultFormulaVisitor<TraversalProcess>() {
      @Override
      protected TraversalProcess visitDefault(Formula f) {
        return TraversalProcess.CONTINUE;
      }

      @Override
      public TraversalProcess visitFunction(
          Formula f,
          List<Formula> args,
          FunctionDeclaration<?> functionDeclaration) {
        if (functionDeclaration.getKind() == FunctionDeclarationKind.UF) {
          if (fmgr.isIntermediate(functionDeclaration.getName(), pSSAMap)) {
            out.set(true);
            return TraversalProcess.ABORT;
          }
        }
        return TraversalProcess.CONTINUE;
      }
    }, atom);
    return out.get();
  }
}
