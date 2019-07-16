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
package org.sosy_lab.cpachecker.util;

import com.google.common.collect.ImmutableList;

/**
 * This class provides methods for checking whether a function is a specific builtin for handling
 * strings. We support some additional verifier functions for providing constraints
 */
public class BuiltinStringFunctions {
  private static final String INREGEX = "__VERIFIER_inregex";
  private static final String STRLEN = "strlen";

  private static final ImmutableList<String> possibleFunctions =
      ImmutableList.of(INREGEX, STRLEN);

  public static boolean isBuiltinStringFunction(String pFunctionName) {
    for (String fun : possibleFunctions) {
      if (pFunctionName.equals(fun)) {
        return true;
      }
    }
    return false;
  }

  public static boolean matchesInRegex(String pFunctionName) {
    return pFunctionName.equals(INREGEX);
  }

  public static boolean matchesStrlen(String pFunctionName) {
    return pFunctionName.equals(STRLEN);
  }
}
