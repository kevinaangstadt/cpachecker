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

public class RegexConcat implements RegexOperator {
  private final RegexOperator re1, re2;

  public static RegexConcat of(RegexOperator re1, RegexOperator re2) {
    return new RegexConcat(re1, re2);
  }

  public RegexConcat(RegexOperator re1, RegexOperator re2) {
    this.re1 = re1;
    this.re2 = re2;
  }

  @Override
  public String accept(RegexFormulaVisitor pV) {
    return pV.visit(this);
  }

  @Override
  public String toString() {
    return "(" + this.re1.toString() + ")(" + this.re2.toString() + ")";
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
    RegexConcat other = (RegexConcat) obj;
    return this.re1.equals(other.re1) && this.re2.equals(other.re2);
  }

  @Override
  public int hashCode() {
    final int prime = 71;
    int result = 1;
    result = prime * result + this.re1.hashCode();
    result = prime * result + this.re2.hashCode();
    return result;
  }

  @Override
  public RegexFormula toFormula(StringFormulaManagerView pSfmgr) {
    return pSfmgr.regexConcat(re1.toFormula(pSfmgr), re2.toFormula(pSfmgr));
  }
}
