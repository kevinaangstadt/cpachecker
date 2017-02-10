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
package org.sosy_lab.cpachecker.util.predicates.pathformula.strings;

import java.util.Optional;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.core.AnalysisDirection;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCCodeException;
import org.sosy_lab.cpachecker.util.VariableClassification;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.arrays.CToFormulaConverterWithArrays;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.Constraints;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.CtoFormulaTypeHandler;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ctoformula.FormulaEncodingOptions;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.StringFormulaManagerView;
import org.sosy_lab.java_smt.api.Formula;

public class CToFormulaConverterWithArraysAndStrings extends CToFormulaConverterWithArrays {
  protected final StringFormulaManagerView sfmgr;

  public CToFormulaConverterWithArraysAndStrings(
      FormulaEncodingOptions pOptions, FormulaManagerView pFmgr,
      MachineModel pMachineModel, Optional<VariableClassification> pVariableClassification, LogManager pLogger,
      ShutdownNotifier pShutdownNotifier, CtoFormulaTypeHandler pTypeHandler, AnalysisDirection pDirection) {

    super(pOptions, pFmgr, pMachineModel, pVariableClassification, pLogger, pShutdownNotifier, pTypeHandler, pDirection);

    this.sfmgr = pFmgr.getStringFormulaManager();
  }

  @Override
  protected Formula makeVariable(String pName, CType pType, SSAMapBuilder pSsa) {
    // Overwritten to make it visible for ExpressionToFormulaVisitorWithArraysAndString
    return super.makeVariable(pName, pType, pSsa);
  }

  @Override
  protected Formula makeCast(CType pFromType, CType pToType, Formula pFormula, Constraints pConstraints, CFAEdge pEdge)
      throws UnrecognizedCCodeException {
    return super.makeCast(pFromType, pToType, pFormula, pConstraints, pEdge);
  }


}
