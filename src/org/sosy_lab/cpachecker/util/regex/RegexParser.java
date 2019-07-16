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

import static com.google.common.base.Preconditions.checkNotNull;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexOperator;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParser;
import org.sosy_lab.cpachecker.util.regex.generated.RegexGrammarParserBaseVisitor;
import org.sosy_lab.cpachecker.util.regex.generated.RegexLexer;

public abstract class RegexParser extends RegexGrammarParserBaseVisitor<RegexOperator> {

  private final CharStream input;

  private RegexParser(CharStream pInput) {
    input = checkNotNull(pInput);
  }

  public static RegexOperator parseRegex(String pRaw) throws RegexParseException {
    checkNotNull(pRaw);
    return new RegexFormulaParser(CharStreams.fromString(pRaw)).doParse();
  }

  abstract ParseTree getParseTree(RegexGrammarParser pParser);

  RegexOperator doParse() throws RegexParseException {
    try {
      // Tokenize the stream
      RegexLexer lexer = new RegexLexer(input);
      // Raise an exception instead of printing long error messages on the console
      // For more informations, see https://stackoverflow.com/a/26573239/8204996
      lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
      // Add a fail-fast behavior for token errors
      lexer.addErrorListener(RegexParserErrorListener.INSTANCE);

      CommonTokenStream tokens = new CommonTokenStream(lexer);

      // Parse the tokens
      RegexGrammarParser parser = new RegexGrammarParser(tokens);
      parser.removeErrorListeners();
      parser.addErrorListener(RegexParserErrorListener.INSTANCE);

      RegexOperatorTreeVisitor visitor = new RegexOperatorTreeVisitor();
      ParseTree tree = getParseTree(parser);

      return visitor.visit(tree);
    } catch (ParseCancellationException e) {
      throw new RegexParseException(e.getMessage(), e);
    }
  }

  private static class RegexFormulaParser extends RegexParser {

    RegexFormulaParser(CharStream input) {
      super(input);
    }

    @Override
    ParseTree getParseTree(RegexGrammarParser parser) {
      return parser.regex();
    }
  }
}
