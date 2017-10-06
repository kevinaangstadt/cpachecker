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


import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CArrayType;
import org.sosy_lab.cpachecker.cfa.types.c.CBasicType;
import org.sosy_lab.cpachecker.cfa.types.c.CPointerType;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.cfa.types.c.CTypedefType;
import org.sosy_lab.cpachecker.util.predicates.pathformula.arrays.CtoFormulaTypeHandlerWithArrays;
import org.sosy_lab.java_smt.api.FormulaType;


public class TypeHandlerWithArraysAndStrings extends CtoFormulaTypeHandlerWithArrays {
  public TypeHandlerWithArraysAndStrings(
      LogManager pLogger,
      MachineModel pMachineModel) {
    super(pLogger, pMachineModel);
  }

  @Override
  public FormulaType<?> getFormulaTypeFromCType(CType pType) {
    if (pType instanceof CSimpleType) {
      CSimpleType simpleType = (CSimpleType) pType;
      switch (simpleType.getType()) {
        case INT:
          return FormulaType.fromString(FormulaType.IntegerType.toString());
        case CHAR:
          return FormulaType.getStringType();
        default:
          break;
      }
    } else if (pType instanceof CArrayType) {
      final CArrayType at = (CArrayType) pType;
      if(at.getType() instanceof CSimpleType) {
        final CSimpleType et = (CSimpleType) at.getType();
        if(et.getType() == CBasicType.CHAR) {
          //we have a string
          return FormulaType.getStringType();
        }
      }
    } else if (pType instanceof CPointerType) {
      final CPointerType pt = (CPointerType) pType;
      if(pt.getType() instanceof CSimpleType) {
        final CSimpleType et = (CSimpleType) pt.getType();
        if(et.getType() == CBasicType.CHAR) {
          //we have a string
          return FormulaType.getStringType();
        }
      }
    } else if (pType instanceof CTypedefType) {
      final CTypedefType dt = (CTypedefType) pType;
      return getFormulaTypeFromCType(dt.getRealType());
    }
    return super.getFormulaTypeFromCType(pType);
  }
}
