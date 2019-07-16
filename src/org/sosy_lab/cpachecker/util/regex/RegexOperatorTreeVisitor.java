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
package org.sosy_lab.cpachecker.util.regex;

import org.sosy_lab.cpachecker.util.regex.formulas.RegexCar;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexConcat;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexEps;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexNull;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexOperator;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexPlus;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexStar;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexUnion;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.RegexContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_carContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_concatContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_epsContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_nullContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_plusContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_starContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_typeContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser.Regex_unionContext;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParserBaseVisitor;

public class RegexOperatorTreeVisitor extends RegexGrammarParserBaseVisitor<RegexOperator> {

  @Override
  public RegexOperator visitRegex(RegexContext ctx) {
    // regex -> regex_type EOF
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 2);

    return visit(ctx.getChild(0));
  }

  @Override
  public RegexOperator visitRegex_type(Regex_typeContext ctx) {
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 1);

    return visit(ctx.getChild(0));
  }

  @Override
  public RegexOperator visitRegex_null(Regex_nullContext ctx) {
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 1);
    return new RegexNull();
  }

  @Override
  public RegexOperator visitRegex_union(Regex_unionContext ctx) {
    // regex_union -> LPAREN regex RPAREN UNION LPAREN regex RPAREN
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 7);

    RegexOperator re1 = visit(ctx.getChild(1));
    RegexOperator re2 = visit(ctx.getChild(5));

    return RegexUnion.of(re1, re2);
  }

  @Override
  public RegexOperator visitRegex_car(Regex_carContext ctx) {
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 1);
    return RegexCar.of(ctx.getChild(0).getText());
  }

  @Override
  public RegexOperator visitRegex_plus(Regex_plusContext ctx) {
    // regex_plus -> LPAREN regex RPAREN PLUS
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 4);
    return RegexPlus.of(visit(ctx.getChild(1)));
  }

  @Override
  public RegexOperator visitRegex_star(Regex_starContext ctx) {
    // regex_star -> LPAREN regex RPAREN STAR
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 4);
    return RegexStar.of(visit(ctx.getChild(1)));
  }

  @Override
  public RegexOperator visitRegex_concat(Regex_concatContext ctx) {
    // regex_concat -> LPAREN regex RPAREN LPAREN regex RPAREN
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 6);

    RegexOperator re1 = visit(ctx.getChild(1));
    RegexOperator re2 = visit(ctx.getChild(4));

    return RegexConcat.of(re1, re2);
  }

  @Override
  public RegexOperator visitRegex_eps(Regex_epsContext ctx) {
    throwException_WhenInvalidChildCount(ctx.getChildCount(), 1);
    return new RegexEps();
  }

  private void throwException_WhenInvalidChildCount(int pActual, int pExpected) {
    if (pActual == pExpected) {
      return;
    }

    throw new RuntimeException(
        String.format(
            "Invalid input provided. Expected %d child-nodes in param 'ctx', however %d were found",
            pExpected,
            pActual));
  }
}
