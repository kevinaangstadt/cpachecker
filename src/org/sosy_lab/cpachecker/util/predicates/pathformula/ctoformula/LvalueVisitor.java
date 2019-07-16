/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula;

import static org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.CtoFormulaTypeUtils.getRealFieldOwner;

import java.util.Optional;
import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CComplexCastExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFieldReference;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CPointerExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CSimpleDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.c.CUnaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.DefaultCExpressionVisitor;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.types.c.CPointerType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCodeException;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ErrorConditions;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.pointeraliasing.PointerTargetSetBuilder;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.StringFormula;

class LvalueVisitor extends DefaultCExpressionVisitor<Formula, UnrecognizedCodeException> {

  private final CtoFormulaConverter conv;
  private final CFAEdge       edge;
  private final String        function;
  private final SSAMapBuilder ssa;
  private final PointerTargetSetBuilder pts;
  private final Constraints   constraints;
  private final ErrorConditions errorConditions;

  LvalueVisitor(CtoFormulaConverter pConv, CFAEdge pEdge, String pFunction, SSAMapBuilder pSsa,
      PointerTargetSetBuilder pPts, Constraints pConstraints, ErrorConditions pErrorConditions) {

    conv = pConv;
    edge = pEdge;
    function = pFunction;
    ssa = pSsa;
    pts = pPts;
    constraints = pConstraints;
    errorConditions = pErrorConditions;
  }

  @Override
  protected BitvectorFormula visitDefault(CExpression exp) throws UnrecognizedCodeException {
    throw new UnrecognizedCodeException("Unknown lvalue", edge, exp);
  }

  @Override
  public Formula visit(CIdExpression idExp) {
    return conv.makeFreshVariable(idExp.getDeclaration().getQualifiedName(), idExp.getExpressionType(), ssa);
  }

  /**  This method is called when we don't know what else to do. */
  private Formula giveUpAndJustMakeVariable(CExpression exp) {
    return conv.makeVariableUnsafe(exp, function, ssa, true);
  }

  @Override
  public Formula visit(CUnaryExpression pE) throws UnrecognizedCodeException {
    return giveUpAndJustMakeVariable(pE);
  }

  @Override
  public Formula visit(CComplexCastExpression pE) throws UnrecognizedCodeException {
    if (pE.isImaginaryCast()) {
      throw new UnrecognizedCodeException("Unknown lvalue", edge, pE);
    }
    // TODO complex numbers are not supported for evaluation right now
    return giveUpAndJustMakeVariable(pE);
  }

  @Override
  public Formula visit(CPointerExpression pE) throws UnrecognizedCodeException {
    return giveUpAndJustMakeVariable(pE);
  }

  @Override
  public Formula visit(CFieldReference fexp) throws UnrecognizedCodeException {
    if (!conv.options.handleFieldAccess()) {
      CExpression fieldRef = fexp.getFieldOwner();
      if (fieldRef instanceof CIdExpression) {
        CSimpleDeclaration decl = ((CIdExpression) fieldRef).getDeclaration();
        if (decl instanceof CDeclaration && ((CDeclaration)decl).isGlobal()) {
          // this is the reference to a global field variable
          // we don't need to scope the variable reference
          String var = CtoFormulaConverter.exprToVarNameUnscoped(fexp);

          return conv.makeFreshVariable(var, fexp.getExpressionType(), ssa);
        }
      }
      return giveUpAndJustMakeVariable(fexp);
    }

    // s.a = ...
    // s->b = ...
    // make a new s and return the formula accessing the field
    // as constraint add that all other fields (the rest of the bitvector) remains the same.
    CExpression owner = getRealFieldOwner(fexp);
    // This will just create the formula with the current ssa-index.
    Formula oldStructure = conv.buildTerm(owner, edge, function, ssa, pts, constraints, errorConditions);
    // This will eventually increment the ssa-index and return the new formula.
    Formula newStructure = owner.accept(this);

    // Other fields did not change.
    Formula oldRestS = conv.replaceField(fexp, oldStructure, Optional.empty());
    Formula newRestS = conv.replaceField(fexp, newStructure, Optional.empty());
    constraints.addConstraint(conv.fmgr.makeEqual(oldRestS, newRestS));

    Formula fieldFormula = conv.accessField(fexp, newStructure);
    return fieldFormula;
  }

  @Override
  public Formula visit(CArraySubscriptExpression pE) throws UnrecognizedCodeException {
    final CType exp_type = pE.getExpressionType();
    if (CtoFormulaConverter.isStringType(exp_type)) {
      final CExpression arrayExpression = pE.getArrayExpression();
      final CExpression subscript = pE.getSubscriptExpression();

      String var = CtoFormulaConverter.exprToVarName(arrayExpression, function);
      // FIXME only allows a single assignment
      Formula base = conv.makeVariable(var, arrayExpression.getExpressionType(), ssa);

      final CType subscriptType = subscript.getExpressionType();
      Formula index =
          conv.makeCast(
              subscriptType,
                                          CPointerType.POINTER_TO_VOID,
              subscript.accept(
                  new ExpressionToFormulaVisitor(
                      conv,
                      conv.fmgr,
                      edge,
                      function,
                      ssa,
                      constraints)),
                                          constraints,
                                          edge);
      // try to make index an integer formula
      if (index instanceof BitvectorFormula) {
        // FIXME is this always unsigned?
        index =
            conv.fmgr.getBitvectorFormulaManager()
                .toIntegerFormula((BitvectorFormula) index, false);
      }

      assert base instanceof StringFormula : base + "should be of type StringFormula";
      assert index instanceof IntegerFormula : index + "should be of type IntegerFormula";
      return conv.sfmgr.charAt((StringFormula) base, (IntegerFormula) index);

    }

    return giveUpAndJustMakeVariable(pE);
  }


}