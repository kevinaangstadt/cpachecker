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


public class SimpleRegexNull extends SimpleRegex {

  SimpleRegexToken null_tok;

  public SimpleRegexNull(SimpleRegexTokenizer t) throws SimpleRegexSyntaxError {
    null_tok = t.next(SimpleRegexToken.Type.NULL);
  }

  @Override
  public RegexFormula toFormula(StringFormulaManager pSfmgr) {
    return pSfmgr.regexEmpty();
  }

  @Override
  public int getCol() {
    return null_tok.col;
  }

}
