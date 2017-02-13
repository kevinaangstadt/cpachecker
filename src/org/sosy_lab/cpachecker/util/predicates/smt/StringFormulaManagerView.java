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
package org.sosy_lab.cpachecker.util.predicates.smt;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.RegexFormula;
import org.sosy_lab.java_smt.api.StringFormula;
import org.sosy_lab.java_smt.api.StringFormulaManager;

public class StringFormulaManagerView extends BaseManagerView implements StringFormulaManager {
  private StringFormulaManager manager;

  /**
   * Creates the new formula manager view for strings.
   *
   * @param pWrappingHandler A handler for wrapping and unwrapping of formulae.
   * @param pManager The formula manager capable of the SMT theory of strings.
   */
  StringFormulaManagerView(
      final FormulaWrappingHandler pWrappingHandler, final StringFormulaManager pManager) {
    super(pWrappingHandler);
    this.manager = pManager;
  }

  @Override
  public StringFormula makeString(String pString) {
    return manager.makeString(pString);
  }

  @Override
  public StringFormula makeVariable(String pName) {
    return manager.makeVariable(pName);
  }

  @Override
  public BooleanFormula contains(StringFormula pString, StringFormula pSearch) {
    return manager.contains(pString, pSearch);
  }

  @Override
  public BooleanFormula startsWith(StringFormula pString, StringFormula pPrefix) {
    return manager.startsWith(pString, pPrefix);
  }

  @Override
  public BooleanFormula endsWith(StringFormula pString, StringFormula pSuffix) {
    return manager.endsWith(pString, pSuffix);
  }

  @Override
  public BooleanFormula regexIn(StringFormula pString, RegexFormula pRegex) {
    return manager.regexIn(pString, pRegex);
  }

  @Override
  public IntegerFormula length(StringFormula pString) {
    return manager.length(pString);
  }

  @Override
  public IntegerFormula indexOf(StringFormula pString, StringFormula pSearch, IntegerFormula pInt) {
    return manager.indexOf(pString, pSearch, pInt);
  }

  @Override
  public StringFormula concat(StringFormula pString1, StringFormula pString2) {
    return manager.concat(pString1, pString2);
  }

  @Override
  public StringFormula substring(StringFormula pString, IntegerFormula pStart,
      IntegerFormula pLength) {
    return manager.substring(pString, pStart, pLength);
  }

  @Override
  public StringFormula replace(StringFormula pString, StringFormula pFind, StringFormula pReplace) {
    return manager.replace(pString, pFind, pReplace);
  }

  @Override
  public StringFormula charAt(StringFormula pString, IntegerFormula pIndex) {
    return manager.charAt(pString, pIndex);
  }

  @Override
  public RegexFormula str2Regex(String pString) {
    return manager.str2Regex(pString);
  }

  @Override
  public RegexFormula regexStar(RegexFormula pRegex) {
    return manager.regexStar(pRegex);
  }

  @Override
  public RegexFormula regexPlus(RegexFormula pRegex) {
    return manager.regexPlus(pRegex);
  }

  @Override
  public RegexFormula regexQuestion(RegexFormula pRegex) {
    return manager.regexQuestion(pRegex);
  }

  @Override
  public RegexFormula regexConcat(RegexFormula pRegex1, RegexFormula pRegex2) {
    return manager.regexConcat(pRegex1, pRegex2);
  }

  @Override
  public RegexFormula regexUnion(RegexFormula pRegex1, RegexFormula pRegex2) {
    return manager.regexUnion(pRegex1, pRegex2);
  }

  @Override
  public BooleanFormula equal(StringFormula pString1, StringFormula pString2) {
    return manager.equal(pString1, pString2);
  }

  @Override
  public RegexFormula regexRange(String pStart, String pEnd) {
    return manager.regexRange(pStart, pEnd);
  }
}
