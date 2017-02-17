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

public class SimpleRegexUnion extends SimpleRegex {
  @SuppressWarnings("unused")
  private SimpleRegexToken lparen, union, rparen;
  @SuppressWarnings("unused")
  private SimpleRegex re1, re2;

  // Union -> ( R | R )
  public SimpleRegexUnion(SimpleRegexTokenizer t) throws SimpleRegexSyntaxError {
    int i = t.backup();
    try {
      lparen = t.next(SimpleRegexToken.Type.LPAREN);
      re1 = SimpleRegex.getRegex(t);
      union = t.next(SimpleRegexToken.Type.UNION);
      re2 = SimpleRegex.getRegex(t);
      rparen = t.next(SimpleRegexToken.Type.RPAREN);
    } catch(SimpleRegexSyntaxError e) {
      while(t.numBackups() >= i) {
        t.restore();
      }
      throw e;
    }
  }

  @Override
  public RegexFormula toFormula(StringFormulaManager sfmgr) {
    return sfmgr.regexUnion(re1.toFormula(sfmgr), re2.toFormula(sfmgr));
  }

  @Override
  public int getCol() {
    return rparen.col;
  }
}
