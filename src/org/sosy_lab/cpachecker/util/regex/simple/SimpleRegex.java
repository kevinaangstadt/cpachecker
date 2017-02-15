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

public abstract class SimpleRegex {
  // SimpleRegex -> SimpleRegexUnion | SimpleRegexCar | SimpleRegexStar | SimpleRegexConcat | SimpleRegexEps

  public static SimpleRegex getRegex(SimpleRegexTokenizer tokenizer) {
    try {
      return new SimpleRegexUnion(tokenizer);
    } catch (SimpleRegexSyntaxError e) {
      try {
        return new SimpleRegexCar(tokenizer) ;
      }catch (SimpleRegexSyntaxError e2) {
        try {
          return new SimpleRegexStar(tokenizer);
        } catch (SimpleRegexSyntaxError e3) {
          try {
            return new SimpleRegexConcat(tokenizer);
          } catch (SimpleRegexSyntaxError e4) {
            return new SimpleRegexEps(tokenizer);
          }
        }
      }

    }
  }



  public static RegexFormula parseRegex(String re, StringFormulaManager sfmgr) {
    SimpleRegexTokenizer tokenizer = new SimpleRegexTokenizer(re);
    SimpleRegex reParse;
    try {
      reParse = getRegexUnion(tokenizer);
      if (tokenizer.hasNext()) {
        throw new SimpleRegexSyntaxError(reParse.getCol());
      }
    } catch (SimpleRegexSyntaxError e) {
      try {
        reParse = getRegexCar(tokenizer);
        if (tokenizer.hasNext()) {
          throw new SimpleRegexSyntaxError(reParse.getCol());
        }
      } catch (SimpleRegexSyntaxError e2) {
        try {
          reParse = getRegexStar(tokenizer);
          if (tokenizer.hasNext()) {
            throw new SimpleRegexSyntaxError(reParse.getCol());
          }
        } catch (SimpleRegexSyntaxError e3) {
          try {
            reParse = getRegexConcat(tokenizer);
            if (tokenizer.hasNext()) {
              throw new SimpleRegexSyntaxError(reParse.getCol());
            }
          } catch (SimpleRegexSyntaxError e4) {
            reParse = getRegexEps(tokenizer);
            if (tokenizer.hasNext()) {
              throw new SimpleRegexSyntaxError(reParse.getCol());
            }
          }
        }
      }
    }

    return reParse.toFormula(sfmgr);
  }

  private static SimpleRegex getRegexUnion(SimpleRegexTokenizer tokenizer) throws SimpleRegexSyntaxError {
    return new SimpleRegexUnion(tokenizer);
  }
  private static SimpleRegex getRegexCar(SimpleRegexTokenizer tokenizer) throws SimpleRegexSyntaxError {
    return new SimpleRegexCar(tokenizer);
  }
  private static SimpleRegex getRegexStar(SimpleRegexTokenizer tokenizer) throws SimpleRegexSyntaxError {
    return new SimpleRegexStar(tokenizer);
  }
  private static SimpleRegex getRegexConcat(SimpleRegexTokenizer tokenizer) throws SimpleRegexSyntaxError {
    return new SimpleRegexConcat(tokenizer);
  }
  private static SimpleRegex getRegexEps(SimpleRegexTokenizer tokenizer) throws SimpleRegexSyntaxError {
    return new SimpleRegexEps(tokenizer);
  }

  public abstract RegexFormula toFormula(StringFormulaManager sfmgr);
  public abstract int getCol();
}
