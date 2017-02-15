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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleRegexToken {
  public final String text;
  public final Type type;
  public final int col;


  public SimpleRegexToken(Type type, String text, int col) {
    this.type = type;
    this.text = text;
    this.col = col;
  }

  @Override
  public String toString() {
    return type+"["+text+"]@"+col;
  }

  public enum Type {
    LPAREN("\\("),
    RPAREN("\\)"),
    HEXESCAPE("\\\\x[0-9][0-9]"),
    UNION("\\|"),
    STAR("\\*"),
    EPS("eps");

    private String regExp;
    Type(String regExp) {
      this.regExp = regExp;
    }

    public String getMatch(String code) {
      Pattern pattern = Pattern.compile(regExp);
      Matcher matcher = pattern.matcher(code);

      if(matcher.lookingAt()){
        return matcher.group();
      } else {
        return null;
      }
    }
  }
}
