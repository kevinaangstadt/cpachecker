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

public class RegexRange implements RegexOperator {
  private final int hexVal1, hexVal2;

  public static RegexRange of(int hexVal1, int hexVal2) {
    return new RegexRange(hexVal1, hexVal2);
  }

  public static RegexRange of(String hexVal1, String hexVal2) {
    return new RegexRange(hexVal1, hexVal2);
  }

  public RegexRange(int hexVal1, int hexVal2) {
    this.hexVal1 = hexVal1;
    this.hexVal2 = hexVal2;
  }

  public RegexRange(String hexVal1, String hexVal2) {
    this.hexVal1 = Integer.parseInt(hexVal1.substring(2), 16);
    this.hexVal2 = Integer.parseInt(hexVal2.substring(2), 16);
  }

  @Override
  public String accept(RegexFormulaVisitor pV) {
    return pV.visit(this);
  }

  @Override
  public String toString() {
    return String.format("[\\x%02X-\\x%02X]", this.hexVal1, this.hexVal2);
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
    RegexRange other = (RegexRange) obj;
    return this.hexVal1 == other.hexVal1 && this.hexVal2 == other.hexVal2;
  }

  @Override
  public int hashCode() {
    final int prime = 61;
    return prime * Integer.hashCode(this.hexVal1) + Integer.hashCode(this.hexVal2);
  }

  @Override
  public RegexFormula toFormula(StringFormulaManagerView pSfmgr) {
    String sStart = String.format("\\x%02X", this.hexVal1);
    String sEnd = String.format("\\%02X", this.hexVal2);
    return pSfmgr.regexRange(pSfmgr.makeString(sStart), pSfmgr.makeString(sEnd));
  }
}
