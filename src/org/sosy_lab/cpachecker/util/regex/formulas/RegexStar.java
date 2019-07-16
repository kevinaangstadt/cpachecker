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

public class RegexStar implements RegexOperator {
  private final RegexOperator re;

  public static RegexStar of(RegexOperator re) {
    return new RegexStar(re);
  }

  public RegexStar(RegexOperator re) {
    this.re = re;
  }

  @Override
  public String accept(RegexFormulaVisitor pV) {
    return pV.visit(this);
  }

  @Override
  public String toString() {
    return "(" + this.re.toString() + ")*";
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
    RegexStar other = (RegexStar) obj;
    return this.re.equals(other.re);
  }

  @Override
  public int hashCode() {
    final int prime = 67;
    return prime + this.re.hashCode();
  }

  @Override
  public RegexFormula toFormula(StringFormulaManagerView pSfmgr) {
    return pSfmgr.regexStar(re.toFormula(pSfmgr));
  }
}
