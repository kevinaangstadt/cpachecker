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

import java.util.LinkedList;
import org.sosy_lab.cpachecker.util.regex.simple.SimpleRegexToken.Type;

public class SimpleRegexTokenizer {
  private LinkedList<SimpleRegexToken> tokenQueue;
  private int col;

  @SuppressWarnings("unused")
  public SimpleRegexTokenizer(String inputString) {
    tokenQueue = new LinkedList<SimpleRegexToken>();
    col = 1;
    while(!inputString.isEmpty()) {
      boolean foundMatch = false;
      for(Type t : SimpleRegexToken.Type.values()) {
        String text = t.getMatch(inputString);
        if(text != null) {
          col += text.length();
          tokenQueue.offer(new SimpleRegexToken(t, text, col));
          inputString = inputString.substring(text.length());
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        throw new SimpleRegexSyntaxError(col);
      }
    }
  }

  public boolean hasNext() {
    return !tokenQueue.isEmpty();
  }

  /**
   * Find out if the first token is any of the given types
   * @param pTypes any number of token types
   * @return true if the first token is one of these types
   */
  public boolean hasNext(SimpleRegexToken.Type ...pTypes) {
    if(this.hasNext()) {
      for(SimpleRegexToken.Type t : pTypes) {
        if(tokenQueue.peek().type.equals(t)) {
          return true;
        }
      }
    }
    return false;
  }

  public SimpleRegexToken next() throws SimpleRegexSyntaxError {
    if(!this.hasNext()) {
      throw new SimpleRegexSyntaxError(col);
    }
    return tokenQueue.poll();
  }

  public SimpleRegexToken next(SimpleRegexToken.Type ...pTypes) throws SimpleRegexSyntaxError {
    if(!this.hasNext(pTypes)) {
      SimpleRegexToken t = tokenQueue.peek();
      if(t == null) {
        throw new SimpleRegexSyntaxError(col);
      } else {
        throw new SimpleRegexSyntaxError(t.col);
      }
    }
    return tokenQueue.poll();
  }
}
