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
 
 /*
  * Lexer Rules
  */
lexer grammar RegexLexer;

/*
 * Rules for simple regex syntax
 */

 // Parentheses
 LPAREN             : '(' ;
 RPAREN             : ')' ;
 
 // Hex values
 HEXESCAPE          : HEXPRE NUMBER NUMBER ;
 
 // Operators
 UNION              : '|' ;
 PLUS               : '+' ;
 STAR               : '*' ;
 NOT                : '!' ;
 
 // Simple
 NULL               : 'NULL' ;
 EPS                : 'eps' ;
 
 // Whitespace
 WS                 : [ \t\r\n\f] -> skip ; // skip all WS
 
 /*
  * Inline functions for simpler definitions
  */
fragment NUMBER     : [0-9a-fA-F] ;
fragment HEXPRE     : [0\\]'x' ;