/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.util.predicates.pathformula.strings;

import java.util.Optional;
import java.util.logging.Level;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CAssignment;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CInitializerList;
import org.sosy_lab.cpachecker.cfa.ast.c.CInitializers;
import org.sosy_lab.cpachecker.cfa.ast.c.CLeftHandSide;
import org.sosy_lab.cpachecker.cfa.ast.c.CRightHandSide;
import org.sosy_lab.cpachecker.cfa.ast.c.CRightHandSideVisitor;
import org.sosy_lab.cpachecker.cfa.ast.c.CStringLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CVariableDeclaration;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CDeclarationEdge;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CArrayType;
import org.sosy_lab.cpachecker.cfa.types.c.CBasicType;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.core.AnalysisDirection;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCCodeException;
import org.sosy_lab.cpachecker.util.VariableClassification;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ErrorConditions;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.arrays.CToFormulaConverterWithArrays;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.Constraints;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.CtoFormulaTypeHandler;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.FormulaEncodingOptions;
import org.sosy_lab.cpachecker.util.predicates.pathformula.pointeraliasing.PointerTargetSetBuilder;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.StringFormulaManagerView;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.StringFormula;

public class CToFormulaConverterWithArraysAndStrings extends CToFormulaConverterWithArrays {
  protected final StringFormulaManagerView sfmgr;

  public CToFormulaConverterWithArraysAndStrings(
      FormulaEncodingOptions pOptions, FormulaManagerView pFmgr,
      MachineModel pMachineModel, Optional<VariableClassification> pVariableClassification, LogManager pLogger,
      ShutdownNotifier pShutdownNotifier, CtoFormulaTypeHandler pTypeHandler, AnalysisDirection pDirection) {

    super(pOptions, pFmgr, pMachineModel, pVariableClassification, pLogger, pShutdownNotifier, pTypeHandler, pDirection);

    this.sfmgr = pFmgr.getStringFormulaManager();
  }

  @Override
  protected Formula makeVariable(String pName, CType pType, SSAMapBuilder pSsa) {
    // Overwritten to make it visible for ExpressionToFormulaVisitorWithArraysAndString
    return super.makeVariable(pName, pType, pSsa);
  }

  @Override
  protected CRightHandSideVisitor<Formula, UnrecognizedCCodeException> createCRightHandSideVisitor(CFAEdge pEdge,
      String pFunction, SSAMapBuilder pSsa, PointerTargetSetBuilder pPts, Constraints pConstraints,
      ErrorConditions pErrorConditions) {

    // Create a CRightHandSideVisitor with support for arrays and strings!
    return new ExpressionToFormulaVisitorWithArraysAndStrings(
        this, fmgr, machineModel, pEdge, pFunction, pSsa, pConstraints);
  }

  @Override
  protected Formula makeCast(CType pFromType, CType pToType, Formula pFormula, Constraints pConstraints, CFAEdge pEdge)
      throws UnrecognizedCCodeException {
    return super.makeCast(pFromType, pToType, pFormula, pConstraints, pEdge);
  }

  private StringFormula makeStringVariable(String pName, CType pType, SSAMapBuilder pSsa, boolean bMakeFresh) {
    int index = bMakeFresh ? makeFreshIndex(pName, pType, pSsa) : getIndex(pName, pType, pSsa);
    return (StringFormula) fmgr.makeVariable(this.getFormulaTypeFromCType(pType), pName, index);
  }

  @SuppressWarnings("unchecked")
  protected StringFormula makeAssignedStringVariableForStore (String pName,
      CType pType, SSAMapBuilder pSsa) {

    return makeStringVariable(pName, pType, pSsa, direction == AnalysisDirection.BACKWARD);
  }

  @SuppressWarnings("unchecked")
  protected StringFormula makeAssignedStringVariableForEquivalence(String pName,
      CType pType, SSAMapBuilder pSsa) {

    return makeStringVariable(pName, pType, pSsa, direction == AnalysisDirection.FORWARD);
  }

  @Override
  protected BooleanFormula makeDeclaration(CDeclarationEdge pEdge, String pFunction,
      SSAMapBuilder pSsa, PointerTargetSetBuilder pPts, Constraints pConstraints,
      ErrorConditions pErrorConditions) throws UnrecognizedCCodeException, InterruptedException {

    // if this is a string variable, let's declare it here special


    if (!(pEdge.getDeclaration() instanceof CVariableDeclaration)) {
      // struct prototype, function declaration, typedef etc.
      logfOnce(Level.FINEST, pEdge, "Ignoring declaration");
      return bfmgr.makeTrue();
    }

    CVariableDeclaration decl = (CVariableDeclaration)pEdge.getDeclaration();

    CType elementCType = decl.getType();

    if(getFormulaTypeFromCType(elementCType).isStringType()) {
      final String varName = decl.getQualifiedName();

      if (!isRelevantVariable(decl)) {
        logger.logfOnce(Level.FINEST, "%s: Ignoring declaration of unused variable: %s",
            decl.getFileLocation(), decl.toASTString());
        return bfmgr.makeTrue();
      }

      checkForLargeArray(pEdge, decl.getType().getCanonicalType());

      if (options.useParameterVariablesForGlobals() && decl.isGlobal()) {
        globalDeclarations.add(decl);
      }

      // just increment index of variable in SSAMap
      // (a declaration contains an implicit assignment, even without initializer)
      // In case of an existing initializer, we increment the index twice
      // (here and below) so that the index 2 only occurs for uninitialized variables.
      // DO NOT OMIT THIS CALL, even without an initializer!
      if (direction == AnalysisDirection.FORWARD) {
        makeFreshIndex(varName, elementCType, pSsa);
      }

      BooleanFormula result = bfmgr.makeTrue();

      if(decl.getInitializer() instanceof CInitializerList) {
        int size = machineModel.getSizeof(elementCType);
        if (size > 0) {
          Formula var = makeVariable(varName, elementCType, pSsa);
          Formula zero = sfmgr.makeString("");
          result = bfmgr.and(result, fmgr.assignment(var, zero));
        }
      }

      for (CAssignment assignment : CInitializers.convertToAssignments(decl, pEdge)) {
        result = bfmgr.and(result,
            makeAssignment(
                assignment.getLeftHandSide(),
                assignment.getRightHandSide(),
                pEdge,
                pFunction,
                pSsa,
                pPts,
                pConstraints,
                pErrorConditions));
      }

      return result;

    } else {
      //this isn't a string
      return super.makeDeclaration(pEdge, pFunction, pSsa, pPts, pConstraints, pErrorConditions);
    }
  }

  @Override
  protected BooleanFormula makeAssignment(CLeftHandSide pLhs, CLeftHandSide pLhsForChecking, CRightHandSide pRhs,
      CFAEdge pEdge, String pFunction, SSAMapBuilder pSsa, PointerTargetSetBuilder pPts, Constraints pConstraints,
      ErrorConditions pErrorConditions) throws UnrecognizedCCodeException, InterruptedException {

    // check if this a string assignment to a char array
    if (pLhs instanceof CIdExpression
        && ((CIdExpression) pLhs).getExpressionType() instanceof CArrayType) {
      final CArrayType at = (CArrayType) ((CIdExpression) pLhs).getExpressionType();
      if(at.getType() instanceof CSimpleType) {
        final CSimpleType et = (CSimpleType) at.getType();
        if(et.getType() == CBasicType.CHAR) {
          // this should be a string we're assigning to
          // now check that the RHS is a string constant!
          if(pRhs instanceof CStringLiteralExpression) {

            final CIdExpression lhsArrExpr = (CIdExpression) pLhs;
            final String arrayVariableName = lhsArrExpr.getDeclaration().getQualifiedName();

            final SSAMap pSsaBeforeStatement = pSsa.build();
            final SSAMapBuilder lhsSsaBuilder = pSsaBeforeStatement.builder();
            final SSAMapBuilder rhsSsaBuilder = pSsaBeforeStatement.builder();

            final CStringLiteralExpression rhs = (CStringLiteralExpression) pRhs;
            // Make a formula for the rhs
            StringFormula rhsFormula = sfmgr.makeString(rhs.getContentString());

            final StringFormula changedStringFormula = makeAssignedStringVariableForEquivalence(
                arrayVariableName,
                lhsArrExpr.getExpressionType(),
                rhsSsaBuilder);

            // 6. Synchronize the SSA-Map of the LHS and the RHS
            //  (this makes the code for handling a backwards and a forwards analysis simpler)
            final int arrayVariableNewIndex = Math.max(
                lhsSsaBuilder.getIndex(arrayVariableName),
                rhsSsaBuilder.getIndex(arrayVariableName));
            pSsa.setIndex(arrayVariableName, lhsArrExpr.getExpressionType(), arrayVariableNewIndex);

            // 7. Set A2 equivalent to FS (this results in a BooleanFormula)
            return sfmgr.equal(changedStringFormula, rhsFormula);
          }
        }
      }
    }
    // check if this is a char assignment to a string
    else if (pLhs instanceof CArraySubscriptExpression) {
      // a[e]
      final CArraySubscriptExpression lhsExpr = (CArraySubscriptExpression) pLhs;
      if (lhsExpr.getArrayExpression() instanceof CIdExpression) {
        final CType arrayType = lhsExpr.getArrayExpression().getExpressionType();
        if (arrayType instanceof CArrayType) {
          final CArrayType at = (CArrayType) arrayType;
          if (at.getType() instanceof CSimpleType) {
            final CSimpleType et = (CSimpleType) at.getType();
            if(et.getType() == CBasicType.CHAR) {

              final SSAMap pSsaBeforeStatement = pSsa.build();
              final SSAMapBuilder lhsSsaBuilder = pSsaBeforeStatement.builder();
              final SSAMapBuilder rhsSsaBuilder = pSsaBeforeStatement.builder();

              // okay...this should be a string
              final CIdExpression lhsArrExpr = (CIdExpression) lhsExpr.getArrayExpression();
              final String arrayVariableName = lhsArrExpr.getDeclaration().getQualifiedName();

              // 1. Get the (String) formula A1 that represents the LHS (destination of 'store').
              //    The SSA index of this formula stays the same.
              final StringFormula unchangedStringFormula = makeAssignedStringVariableForStore( //Overwrite makeVariable so that it uses a cache of formulas??
                  arrayVariableName,
                  lhsArrExpr.getExpressionType(),
                  lhsSsaBuilder);

              // 2. Get the target index IX. Use a RHS visitor on the subscript expression.
              final IntegerFormula subscriptFormula = (IntegerFormula) buildTerm(lhsExpr.getSubscriptExpression(), pEdge,
                  pFunction, pSsa, pPts, pConstraints, pErrorConditions);

              // 3. Create the formula FR for the RHS (visitor).
              final StringFormula rhsFormula = (StringFormula) buildTerm(pRhs, pEdge,
                  pFunction, pSsa, pPts, pConstraints, pErrorConditions);


              //TODO FIXME is this just a place where I could use charAt for efficiency?

              // 4. Compute a new string formula FS using 'charAt' (this variable has not yet a name in the solver)
              //  (Concat
              //    (Concat
              //      (Substring A1 (MakeNumber 0) IX)
              //      FR
              //    )
              //    (Substring A1 (+ IX (MakeNumber 1)) (Length A1))
              //  )
              final StringFormula storeStringFormula =
                  sfmgr.concat(
                      sfmgr.concat(
                          sfmgr.substring(unchangedStringFormula, fmgr.getIntegerFormulaManager().makeNumber(0), subscriptFormula),
                          rhsFormula
                          ),
                      sfmgr.substring(unchangedStringFormula, fmgr.getIntegerFormulaManager().add(subscriptFormula, fmgr.getIntegerFormulaManager().makeNumber(1)), sfmgr.length(unchangedStringFormula))
                      );

              // 5. Make a new string variable A2 with a new SSA index
              //    (= A2 (= (charAt A1 IX) FR))
              final StringFormula changedStringFormula = makeAssignedStringVariableForEquivalence(
                  arrayVariableName,
                  lhsArrExpr.getExpressionType(),
                  rhsSsaBuilder);

              // 6. Synchronize the SSA-Map of the LHS and the RHS
              //  (this makes the code for handling a backwards and a forwards analysis simpler)
              final int arrayVariableNewIndex = Math.max(
                  lhsSsaBuilder.getIndex(arrayVariableName),
                  rhsSsaBuilder.getIndex(arrayVariableName));
              pSsa.setIndex(arrayVariableName, lhsArrExpr.getExpressionType(), arrayVariableNewIndex);

              // 7. Set A2 equivalent to FS (this results in a BooleanFormula)
              return sfmgr.equal(changedStringFormula, storeStringFormula);
            }
          }
        }
      }
    }
    // this wasn't a string we can handle...oops
    return super.makeAssignment(pLhs, pLhsForChecking, pRhs, pEdge, pFunction, pSsa, pPts, pConstraints, pErrorConditions);
  }

  <T extends Formula> T ifTrueThenOneElseZeroArraysAndStrings(FormulaType<T> type, BooleanFormula pCond) {
    T one = fmgr.makeNumber(type, 1);
    T zero = fmgr.makeNumber(type, 0);
    return bfmgr.ifThenElse(pCond, one, zero);
  }

  /*@Override
  protected BooleanFormula makeFunctionCall(
      final CFunctionCallEdge edge, final String callerFunction,
      final SSAMapBuilder ssa, final PointerTargetSetBuilder pts,
      final Constraints constraints, final ErrorConditions errorConditions)
          throws UnrecognizedCCodeException, InterruptedException {

    CFunctionEntryNode fn = edge.getSuccessor();
    String name = fn.getFunctionName();

    if (name.equals("__cpa_regex")) {
      // this is a regex internal function
      return makeRegexStringComparison(edge, callerFunction, ssa, pts, constraints, errorConditions);
    }

    return super.makeFunctionCall(edge, callerFunction, ssa, pts, constraints, errorConditions);
  }

  private BooleanFormula makeRegexStringComparison(CFunctionCallEdge edge, String callerFunction,
      SSAMapBuilder ssa, PointerTargetSetBuilder pts, Constraints constraints,
      ErrorConditions errorConditions) {

    List<CExpression> actualParams = edge.getArguments();

    CFunctionEntryNode fn = edge.getSuccessor();

    // this function takes a string and a regex string, in that order
    if(actualParams.size() != 2) {
      throw new UnrecognizedCodeException("Number of parameters on function call does " +
          "not match function definition", edge);
    }

    // get the two parameters now
    CExpression strExpression = actualParams.get(0);
    CExpression reExpression = actualParams.get(1);


    RegexFormula re = parseRegexString();


  }*/
}