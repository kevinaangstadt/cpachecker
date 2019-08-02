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

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexCar;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexConcat;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexEps;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexNull;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexOperator;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexPlus;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexStar;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexUnion;

public class RegexOperatorParserTest {

  private static List<RegexOperator> out =
      ImmutableList.of(
          new RegexNull(),
          new RegexEps(),
          new RegexCar(0x62),
          new RegexUnion(new RegexCar(0x62), new RegexCar(0x61)),
          new RegexStar(new RegexCar(0x62)),
          new RegexPlus(new RegexCar(0x62)),
          new RegexConcat(new RegexCar(0x62), new RegexCar(0x61)),
          new RegexConcat(
              new RegexUnion(
                  new RegexConcat(
                      new RegexCar(0x62),
                      new RegexConcat(new RegexCar(0x61), new RegexCar(0x61))),
                new RegexUnion(
                  new RegexConcat(
                          new RegexCar(0x61),
                    new RegexConcat(
                      new RegexCar(0x61),
                      new RegexCar(0x61)
                    )
                  ),
                  new RegexUnion(
                    new RegexConcat(
                              new RegexCar(0x62),
                      new RegexConcat(
                                  new RegexCar(0x62),
                        new RegexCar(0x61)
                      )
                    ),
                          new RegexConcat(
                              new RegexCar(0x61),
                      new RegexConcat(
                        new RegexCar(0x62),
                                  new RegexUnion(
                                      new RegexEps(),
                          new RegexUnion(
                                          new RegexCar(0x62),
                                          new RegexCar(0x61)
                          )
                        )
                      )
                    )
                  )
                )
              ),
              new RegexPlus(new RegexCar(0x00))));

  @Test
  public void test_parse() throws RegexParseException {
    List<String> in =
        ImmutableList.of(
            "NULL",
            "eps",
            "0x62",
            "(0x62)|(0x61)",
            "(0x62)*",
            "(0x62)+",
            "(0x62)(0x61)",
            // "(((0x62)((0x61)(0x61)))|((((0x61)((0x61)(0x61)))|((((0x62)((0x62)(0x61)))|((0x61)((0x62)(((eps)|(((0x62)|(0x61))))))))))))((0x00)+)"
            "(((0x62)((0x61)(0x61)))|(((0x61)((0x61)(0x61)))|(((0x62)((0x62)(0x61)))|((0x61)((0x62)((eps)|((0x62)|(0x61))))))))((0x00)+)");

    for (int i = 0; i < in.size(); i++) {
      System.out.println("=====");
      System.out.println(in.get(i));
      System.out.println("----");
      System.out.println(out.get(i));
      System.out.println("=====");
      assertEquals(in.get(i), out.get(i), RegexParser.parseRegex(in.get(i)));
    }
  }

  @Test
  public void test_hex() throws RegexParseException {
    List<String> in =
        ImmutableList.of(
            "NULL",
            "eps",
            "\\x62",
            "(\\x62)|(\\x61)",
            "(\\x62)*",
            "(\\x62)+",
            "(\\x62)(\\x61)",
            // "(((\\x62)((\\x61)(\\x61)))|((((\\x61)((\\x61)(\\x61)))|((((\\x62)((\\x62)(\\x61)))|((\\x61)((\\x62)(((eps)|(((\\x62)|(\\x61))))))))))))((\\x00)+)"
            "(((\\x62)((\\x61)(\\x61)))|(((\\x61)((\\x61)(\\x61)))|(((\\x62)((\\x62)(\\x61)))|((\\x61)((\\x62)((eps)|((\\x62)|(\\x61))))))))((\\x00)+)");

    for (int i = 0; i < in.size(); i++) {
      System.out.println("=====");
      System.out.println(in.get(i));
      System.out.println("----");
      System.out.println(out.get(i));
      System.out.println("=====");
      assertEquals(in.get(i), out.get(i), RegexParser.parseRegex(in.get(i)));
    }
  }
}
