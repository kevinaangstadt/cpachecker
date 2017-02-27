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
package org.sosy_lab.cpachecker.cpa.bam;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sosy_lab.cpachecker.cfa.blocks.Block;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;

public final class BlockSummaryMissingException extends CPATransferException {

  private static final long serialVersionUID = 0xBADCAB1E;

  private final AbstractState state;
  private final AbstractState reducedState;
  private final Precision reducedPrecision;
  private final Block block;
  private final ReachedSet reachedSet;

  public BlockSummaryMissingException(
      AbstractState pState,
      AbstractState pReducedState,
      Precision pReducedPrecision,
      Block pBlock,
      ReachedSet pReachedSet) {
    super("block start found");
    state = checkNotNull(pState);
    reducedState = checkNotNull(pReducedState);
    reducedPrecision = checkNotNull(pReducedPrecision);
    block = checkNotNull(pBlock);
    reachedSet = checkNotNull(pReachedSet);
  }

  public AbstractState getState() {
    return state;
  }

  public AbstractState getReducedState() {
    return reducedState;
  }

  public Precision getReducedPrecision() {
    return reducedPrecision;
  }

  public Block getBlock() {
    return block;
  }

  public ReachedSet getReachedSet() {
    return reachedSet;
  }
}
