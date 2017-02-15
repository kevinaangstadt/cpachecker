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
package org.sosy_lab.cpachecker.util.regex.simple;

import org.sosy_lab.java_smt.api.RegexFormula;
import org.sosy_lab.java_smt.api.StringFormulaManager;


public class SimpleRegexConcat extends SimpleRegex {
  SimpleRegexToken lparen1, rparen1, lparen2, rparen2;
  SimpleRegex re1, re2;

  public SimpleRegexConcat(SimpleRegexTokenizer t) throws SimpleRegexSyntaxError {
    lparen1 = t.next(SimpleRegexToken.Type.LPAREN);
    re1 = SimpleRegex.getRegex(t);
    rparen1 = t.next(SimpleRegexToken.Type.RPAREN);
    lparen2 = t.next(SimpleRegexToken.Type.LPAREN);
    re2 = SimpleRegex.getRegex(t);
    rparen2 = t.next(SimpleRegexToken.Type.RPAREN);
  }
  @Override
  public RegexFormula toFormula(StringFormulaManager pSfmgr) {
    return pSfmgr.regexConcat(re1.toFormula(pSfmgr), re2.toFormula(pSfmgr));
  }
  @Override
  public int getCol() {
    return rparen2.col;
  }

}
