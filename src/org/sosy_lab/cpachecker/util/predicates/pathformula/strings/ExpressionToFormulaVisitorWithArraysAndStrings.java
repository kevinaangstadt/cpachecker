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

import java.util.List;
import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression.BinaryOperator;
import org.sosy_lab.cpachecker.cfa.ast.c.CCharLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CStringLiteralExpression;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CArrayType;
import org.sosy_lab.cpachecker.cfa.types.c.CBasicType;
import org.sosy_lab.cpachecker.cfa.types.c.CPointerType;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCCodeException;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.arrays.ExpressionToFormulaVisitorWithArrays;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.Constraints;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.StringFormulaManagerView;
import org.sosy_lab.cpachecker.util.regex.simple.SimpleRegex;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.RegexFormula;
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
    if (cExp.getCharacter() == '\0') {
      // TODO does this make sense?
      return smgr.makeString("");
    }
    return smgr.makeString(String.valueOf(cExp.getCharacter()));
  }

  @Override
  public Formula visit(CStringLiteralExpression cExp) throws UnrecognizedCCodeException {
    // we just make this a StringFormula constant
    return smgr.makeString(cExp.getContentString());
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

      final CSimpleType et;
      // we need to figure out if this is a character array
      if (arrayType instanceof CArrayType && ((CArrayType) arrayType).getType() instanceof CSimpleType) {
          CArrayType at = (CArrayType) arrayType;
          et = (CSimpleType) at.getType();
      } else if (arrayType instanceof CPointerType && ((CPointerType) arrayType).getType() instanceof CSimpleType) {
        final CPointerType pt = (CPointerType) arrayType;
        et = (CSimpleType) pt.getType();
      } else {
        et = null;
      }
      if(et != null && et.getType() == CBasicType.CHAR) {
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
      return super.visit(pE);

    } else if (pE.getArrayExpression() instanceof CArraySubscriptExpression) {

      //TODO: How am I supposed to handle this case?
      throw new UnrecognizedCCodeException("I don't know what to do yet when there are subexpressions in an array", pE);

    } else {
      throw new UnrecognizedCCodeException("CArraySubscriptExpression: Unknown type of array-expression!", pE);
    }
  }

  @Override
  public Formula visit(CFunctionCallExpression e) throws UnrecognizedCCodeException {
    final CExpression functionNameExpression = e.getFunctionNameExpression();
    final List<CExpression> parameters = e.getParameterExpressions();

    // First let's handle special cases such as assumes, allocations, nondets, external models, etc.
    final String functionName;
    if (functionNameExpression instanceof CIdExpression) {
      functionName = ((CIdExpression)functionNameExpression).getName();
      if (functionName.equals("__cpa_regex")){
        // this function takes a string and a regex string, in that order
        if(parameters.size() == 2) {
          // get the two parameters now
          CExpression strExpression = parameters.get(0);
          CExpression reExpression = parameters.get(1);

          //make sure the types are right
          if((ctfa.getFormulaTypeFromCType(strExpression.getExpressionType()).isStringType())) {
            // the first one should be a string variable or constant

            if (reExpression instanceof CStringLiteralExpression) {
              // the second is the regex and should be a string literal
              String re = ((CStringLiteralExpression) reExpression).getContentString();
              RegexFormula reFormula = SimpleRegex.parseRegex(re,smgr);
              StringFormula strFormula = (StringFormula) toFormula(strExpression);

              BooleanFormula match = smgr.regexIn(strFormula, reFormula);

              FormulaType<?> retT = ctfa.getFormulaTypeFromCType(e.getExpressionType());
              return ctfa.ifTrueThenOneElseZeroArraysAndStrings(retT, match);
            }
          }
        }
      } else if (functionName.equals("__cpa_strlen") || functionName.equals("strlen")) {
        // this function takes a string and returns an int of its length
        if (parameters.size() == 1) {
          CExpression strExpression = parameters.get(0);

          // make sure it is the right type
          if((ctfa.getFormulaTypeFromCType(strExpression.getExpressionType()).isStringType())) {
            // this is a string, yay!
            StringFormula strFormula = (StringFormula) toFormula(strExpression);

            return smgr.length(strFormula);
          }
        }
      } else if (functionName.equals("__cpa_streq")) {
        // this function takes two strings and returns if they're equal
        if (parameters.size() == 2) {
          CExpression strE1 = parameters.get(0);
          CExpression strE2 = parameters.get(1);

          if ((ctfa.getFormulaTypeFromCType(strE1.getExpressionType()).isStringType()) &&
              (ctfa.getFormulaTypeFromCType(strE2.getExpressionType()).isStringType())
              ) {
            StringFormula strF1 = (StringFormula) toFormula(strE1);
            StringFormula strF2 = (StringFormula) toFormula(strE2);

            BooleanFormula eq = smgr.equal(strF1, strF2);

            FormulaType<?> retT = ctfa.getFormulaTypeFromCType(e.getExpressionType());
            return ctfa.ifTrueThenOneElseZeroArraysAndStrings(retT, eq);
          }
        }
      }
    }

    return super.visit(e);
  }
}
