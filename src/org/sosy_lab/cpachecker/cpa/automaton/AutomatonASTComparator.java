/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2010  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.automaton;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sosy_lab.cpachecker.cfa.CParser;
import org.sosy_lab.cpachecker.cfa.ast.IASTExpressionStatement;
import org.sosy_lab.cpachecker.cfa.ast.IASTIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.IASTLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.IASTName;
import org.sosy_lab.cpachecker.cfa.ast.IASTNode;
import org.sosy_lab.cpachecker.cfa.ast.IASTStatement;
import org.sosy_lab.cpachecker.exceptions.ParserException;

import com.google.common.base.Preconditions;

/**
 * Provides methods for generating, comparing and printing the ASTs generated from String.
 * The ASTs are generated by the Eclipse CDT IDE plugin.
 * @author rhein
 */
public class AutomatonASTComparator {

  /**
   * Every occurrence of the joker expression $? in the pattern is substituted by JOKER_EXPR.
   * This is necessary because the C-parser cannot parse the pattern if it contains Dollar-Symbols.
   * The JOKER_EXPR must be a valid C-Identifier. It will be used to recognize the jokers in the generated AST.
   */
  private static final String JOKER_EXPR = "CPAChecker_AutomatonAnalysis_JokerExpression";
  private static final String NUMBERED_JOKER_EXPR = "CPAChecker_AutomatonAnalysis_JokerExpression_Num";
  private static final Pattern NUMBERED_JOKER_PATTERN = Pattern.compile("\\$\\d+");
  
  private static String replaceJokersInPattern(String pPattern) {
    String tmp = pPattern.replaceAll("\\$\\?", " " + JOKER_EXPR + " ");
    Matcher matcher = NUMBERED_JOKER_PATTERN.matcher(tmp);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, "");
      String key = tmp.substring(matcher.start()+1, matcher.end());
      try {
        int varKey = Integer.parseInt(key);
        result.append(" " + NUMBERED_JOKER_EXPR + varKey + " ");
      } catch (NumberFormatException e) {
        // did not work, but i cant log it down here. Should not be able to happen anyway (regex captures only ints)
        result.append(matcher.group());
      }
    }
    matcher.appendTail(result);
    return result.toString();
  }

  static IASTNode generatePatternAST(String pPattern) throws InvalidAutomatonException {
    // $?-Jokers, $1-Jokers and function declaration
    String tmp = addFunctionDeclaration(replaceJokersInPattern(pPattern));

    return AutomatonASTComparator.parse(tmp);
  }

  static IASTNode generateSourceAST(String pSource) throws InvalidAutomatonException {
    String tmp = addFunctionDeclaration(pSource);

    return AutomatonASTComparator.parse(tmp);
  }

  /**
   * Surrounds the argument with a function declaration.
   * This is necessary so the string can be parsed by the CDT parser.
   * @param pBody
   * @return "void test() { " + body + ";}";
   */
  private static String addFunctionDeclaration(String pBody) {
    if (pBody.trim().endsWith(";")) {
      return "void test() { " + pBody + "}";
    } else {
      return "void test() { " + pBody + ";}";
    }
  }

  /**
   * Recursive method for comparing the ASTs.
   */
  static boolean compareASTs(IASTNode pCode, IASTNode pPattern, AutomatonExpressionArguments pArgs) {
    Preconditions.checkNotNull(pCode);
    Preconditions.checkNotNull(pPattern);
    Preconditions.checkNotNull(pArgs);

    if (isJoker(pPattern)) {
      return true;

    } else if (handleNumberJoker(pCode, pPattern, pArgs)) {
      return true;

    } else if (pCode instanceof IASTExpressionStatement) {
      return compareASTs(((IASTExpressionStatement)pCode).getExpression(), pPattern, pArgs);

    } else if (pCode.getClass().equals(pPattern.getClass())) {
      if (pCode instanceof IASTName && ! IASTNamesAreEqual((IASTName)pCode, (IASTName)pPattern)) {
        return false;

      } else if (pCode instanceof IASTLiteralExpression && ! IASTLiteralExpressionsAreEqual((IASTLiteralExpression)pCode, (IASTLiteralExpression)pPattern)) {
        return false;

      } else if (pCode.getChildren().length != pPattern.getChildren().length) {
        return false;

      } else {
        for (int i = 0; i < pCode.getChildren().length; i++) {
          if (compareASTs(pCode.getChildren()[i], pPattern.getChildren()[i], pArgs) == false) {
            return false;
          }
        }
        return true;
      }
    } else {
      return false;
    }
  }

  private static boolean handleNumberJoker(IASTNode pSource, IASTNode pPotentialJoker,
      AutomatonExpressionArguments pArgs) {
    boolean isJoker = false;
    String number = "";
    if (pPotentialJoker instanceof IASTName) {
      IASTName name = (IASTName) pPotentialJoker;
      String strName = String.copyValueOf(name.getSimpleID());
      if (strName.startsWith(NUMBERED_JOKER_EXPR)) {
        isJoker = true;
        number =  strName.substring(NUMBERED_JOKER_EXPR.length());
      }
      // are there more IASTsomethings that could be Jokers?
    } else if (pPotentialJoker instanceof IASTIdExpression) {
      IASTIdExpression name = (IASTIdExpression) pPotentialJoker;
      if (name.getRawSignature().startsWith(NUMBERED_JOKER_EXPR)) {
        isJoker = true;
        number =  name.getRawSignature().substring(NUMBERED_JOKER_EXPR.length());
      }
    }
    if (isJoker) {
      // RawSignature returns the raw code before preprocessing.
      // This does not matter in this case because only very small sniplets, generated by method "addFunctionDeclaration" are tested, no preprocessing
      String value = pSource.getRawSignature();
      pArgs.putTransitionVariable(Integer.parseInt(number),value);
      return true;
    } else {
      return false;
    }
  }

  private static boolean isJoker(IASTNode pNode) {
    if (pNode instanceof IASTName) {
      IASTName name = (IASTName) pNode;
      return String.copyValueOf(name.getSimpleID()).equals(JOKER_EXPR);
      // are there more IASTsomethings that could be Jokers?

    } else if (pNode instanceof IASTIdExpression) {
      IASTIdExpression name = (IASTIdExpression) pNode;
      return name.getRawSignature().equals(JOKER_EXPR);

    } else {
      return false;
    }
  }

  private static boolean IASTNamesAreEqual(IASTName pA, IASTName pB) {
    return Arrays.equals(pA.getSimpleID(), pB.getSimpleID());
  }

  private static boolean IASTLiteralExpressionsAreEqual(IASTLiteralExpression pA, IASTLiteralExpression pB) {
    return Arrays.equals(pA.getValue(), pB.getValue());
  }

  /**
   * Parse the content of a file into an AST with the Eclipse CDT parser.
   * If an error occurs, the program is halted.
   *
   * @param code The C code to parse.
   * @return The AST.
   * @throws InvalidAutomatonException
   */
  private static IASTNode parse(String code) throws InvalidAutomatonException {
    IASTStatement statement;
    try {
      CParser parser = CParser.Factory.getParser(null, CParser.Dialect.C99);
      statement = parser.parseSingleStatement(code);
    } catch (ParserException e) {
      throw new InvalidAutomatonException("Error during parsing C code \""
          + code + "\": " + e.getMessage());
    }

    if (statement instanceof IASTExpressionStatement) {
      return ((IASTExpressionStatement)statement).getExpression();
    } else {
      return statement;
    }
  }

}
