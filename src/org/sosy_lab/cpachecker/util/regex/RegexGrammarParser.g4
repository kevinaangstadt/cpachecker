/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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
parser grammar RegexGrammarParser;

options {
  tokenVocab = RegexLexer;
  language = Java;
}

/*
 * Parser Rules
 */
 
 regex : regex_type EOF ;
 
 regex_type : regex_null | regex_union | regex_car | regex_plus | regex_star | regex_concat | regex_eps ;
 
 regex_null : NULL ;
 
 regex_union : LPAREN regex_type RPAREN UNION LPAREN regex_type RPAREN ;
 
 regex_car : HEXESCAPE ;
 
 regex_plus : LPAREN regex_type RPAREN PLUS ;
 
 regex_star : LPAREN regex_type RPAREN STAR ;
 
 regex_concat : LPAREN regex_type RPAREN LPAREN regex_type RPAREN ;
 
 regex_eps : EPS ;