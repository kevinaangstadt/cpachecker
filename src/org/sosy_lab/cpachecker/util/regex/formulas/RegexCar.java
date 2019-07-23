/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
 */
package org.sosy_lab.cpachecker.util.regex.formulas;

import org.sosy_lab.cpachecker.util.predicates.smt.StringFormulaManagerView;
import org.sosy_lab.cpachecker.util.regex.RegexFormulaVisitor;
import org.sosy_lab.java_smt.api.RegexFormula;

public class RegexCar implements RegexOperator {
  private final int hexVal;

  public static RegexCar of(int hexVal) {
    return new RegexCar(hexVal);
  }

  public static RegexCar of(String hexVal) {
    return new RegexCar(hexVal);
  }

  public RegexCar(int hexVal) {
    this.hexVal = hexVal;
  }

  public RegexCar(String hexVal) {
    this.hexVal = Integer.parseInt(hexVal.substring(2), 16);
  }

  @Override
  public String accept(RegexFormulaVisitor pV) {
    return pV.visit(this);
  }

  @Override
  public String toString() {
    return String.format("\\x%02X", this.hexVal);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RegexCar other = (RegexCar) obj;
    return this.hexVal == other.hexVal;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(this.hexVal);
  }

  @Override
  public RegexFormula toFormula(StringFormulaManagerView pSfmgr) {
    // https://stackoverflow.com/questions/32205446/
    // Strings are modified UTF-8 in the jni
    // use escaping to get around this
    return pSfmgr.str2Regex(pSfmgr.makeString(toString()));
  }
}
