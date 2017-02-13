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

import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression.BinaryOperator;
import org.sosy_lab.cpachecker.cfa.ast.c.CCharLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CArrayType;
import org.sosy_lab.cpachecker.cfa.types.c.CBasicType;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCCodeException;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.arrays.ExpressionToFormulaVisitorWithArrays;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.Constraints;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.StringFormulaManagerView;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.StringFormula;


public class ExpressionToFormulaVisitorWithArraysAndStrings
extends ExpressionToFormulaVisitorWithArrays {

  private final StringFormulaManagerView smgr;
  private final CToFormulaConverterWithArraysAndStrings ctfa;
  private final MachineModel machine;

  public ExpressionToFormulaVisitorWithArraysAndStrings(
      CToFormulaConverterWithArraysAndStrings pCtoFormulaConverter,
      FormulaManagerView pMgr,
      MachineModel pMachineModel,
      CFAEdge pEdge,
      String pFunction,
      SSAMapBuilder pSsa,
      Constraints pConstraints) {
    super(pCtoFormulaConverter, pMgr, pMachineModel, pEdge, pFunction, pSsa, pConstraints);

    smgr = mgr.getStringFormulaManager();
    ctfa = pCtoFormulaConverter;
    machine = pMachineModel;
  }

  @Override
  public Formula visit(CBinaryExpression cExp) throws UnrecognizedCCodeException {
    /*
     * We need to compare this as strings if they're chars
     * otherwise, we do what was previously defined
     */

    CType op1Type = cExp.getOperand1().getExpressionType();
    CType op2Type = cExp.getOperand2().getExpressionType();


    if (op1Type instanceof CSimpleType && op2Type instanceof CSimpleType
        && ((CSimpleType) op1Type).getType() == CBasicType.CHAR
        && ((CSimpleType) op2Type).getType() == CBasicType.CHAR) {

      // both operands are chars
      // we can do string binary operation

      final StringFormula f1 = (StringFormula) processOperand(cExp.getOperand1(), op1Type, op1Type);
      final StringFormula f2 = (StringFormula) processOperand(cExp.getOperand2(), op2Type, op2Type);

      final BinaryOperator op = cExp.getOperator();
      final BooleanFormula result;
      switch(op) {
        case EQUALS:
          result = smgr.equal(f1,f2);
          break;
        case NOT_EQUALS:
          result = mgr.makeNot(smgr.equal(f1, f2));
          break;
        default:
          throw new AssertionError();
      }
      FormulaType<?> returnType = ctfa.getFormulaTypeFromCType(cExp.getExpressionType());
      return ctfa.ifTrueThenOneElseZeroArraysAndStrings(returnType, result);
    }

    return super.visit(cExp);
  }

  @Override
  public Formula visit(CCharLiteralExpression cExp) throws UnrecognizedCCodeException {
    // we just make this a StringFormula constant
    return smgr.makeString(String.valueOf(cExp.getCharacter()));
  }

  @Override
  public Formula visit(CArraySubscriptExpression pE) throws UnrecognizedCCodeException {
    // Examples for a CArraySubscriptExpression:
    //
    //    a[2]
    //      .arrayExpression: a
    //      .subscriptExpression: 2
    //      .type: (int)[]
    //
    //    --> (select a 2)
    //
    //    a[3][7]
    //      .type: int
    //      .subscriptExpression: 7
    //      .arrayExpression: CArraySubscriptExpression
    //          .type: (int)[]
    //          .subscriptExpression: 3
    //          .arrayExpression: CIdExpression a
    //
    //    --> (select (select a 7) 3)

    // we only care in the case of strings; otherwise, call parent
    final StringFormula charAt;

    if (pE.getArrayExpression() instanceof CIdExpression) {
      final CIdExpression idExpr = (CIdExpression) pE.getArrayExpression();
      final String arrayVarName = idExpr.getDeclaration().getQualifiedName();
      final CType arrayType = pE.getArrayExpression().getExpressionType();

      // we need to figure out if this is a character array
      if (arrayType instanceof CArrayType) {
        final CArrayType at = (CArrayType) arrayType;
        if (at.getType() instanceof CSimpleType) {
          final CSimpleType et = (CSimpleType) at.getType();
          if(et.getType() == CBasicType.CHAR) {
            // this is an array of chars (we'll treat it as a string
            charAt = (StringFormula) ctfa.makeVariable(arrayVarName, arrayType, ssa);

            // Handling of the index expression --------------------------------------
            // Make a cast of the subscript expression to the type of the array index
            final Formula indexExprFormula = pE.getSubscriptExpression().accept(this);
            final Formula castedIndexExprFormula = ctfa.makeCast(
                pE.getSubscriptExpression().getExpressionType(),
                machine.getPointerEquivalentSimpleType(), // TODO: Is this correct?
                indexExprFormula, null, null);
            // we are dealing with a string
            Formula out = smgr.charAt(charAt, (IntegerFormula) castedIndexExprFormula);
            return out;
          }
        }
      } else {
        return super.visit(pE);
      }

    } else if (pE.getArrayExpression() instanceof CArraySubscriptExpression) {

      //TODO: How am I supposed to handle this case?
      throw new UnrecognizedCCodeException("I don't know what to do yet when there are subexpressions in an array", pE);

    } else {
      throw new UnrecognizedCCodeException("CArraySubscriptExpression: Unknown type of array-expression!", pE);
    }
    // This should never be reached
    return null;
  }
}
