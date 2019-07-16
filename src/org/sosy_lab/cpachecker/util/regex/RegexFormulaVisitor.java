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
import org.sosy_lab.cpachecker.util.regex.formulas.RegexPlus;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexStar;
import org.sosy_lab.cpachecker.util.regex.formulas.RegexUnion;

public interface RegexFormulaVisitor {
  public String visit(RegexCar pCar);

  public String visit(RegexConcat pConcat);

  public String visit(RegexEps pEps);

  public String visit(RegexNull pNull);

  public String visit(RegexPlus pPlus);

  public String visit(RegexStar pStar);

  public String visit(RegexUnion pUnion);
}
