/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.smg.graphs;

import static com.google.common.truth.Truth.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.cpa.smg.AnonymousTypes;
import org.sosy_lab.cpachecker.cpa.smg.SMGEdgeHasValue;
import org.sosy_lab.cpachecker.cpa.smg.SMGEdgePointsTo;
import org.sosy_lab.cpachecker.cpa.smg.SMGValueFactory;
import org.sosy_lab.cpachecker.cpa.smg.objects.SMGObject;
import org.sosy_lab.cpachecker.cpa.smg.objects.SMGRegion;


public class SMGTest {
  private LogManager logger = LogManager.createTestLogManager();

  private SMG smg;
  CType mockType = AnonymousTypes.createTypeWithLength(32);

  SMGObject obj1 = new SMGRegion(64, "object-1");
  SMGObject obj2 = new SMGRegion(64, "object-2");

  Integer val1 = Integer.valueOf(1);
  Integer val2 = Integer.valueOf(2);

  SMGEdgePointsTo pt1to1 = new SMGEdgePointsTo(val1, obj1, 0);
  SMGEdgeHasValue hv2has2at0 = new SMGEdgeHasValue(mockType, 0, obj2, val2);
  SMGEdgeHasValue hv2has1at4 = new SMGEdgeHasValue(mockType, 32, obj2, val1);

  // obj1 = xxxxxxxx
  // obj2 = yyyyzzzz
  // val1 -> obj1
  // yyyy has value 2
  // zzzz has value 1

  private static SMG getNewSMG64() {
    return new SMG(MachineModel.LINUX64);
  }

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    smg = getNewSMG64();

    smg.addObject(obj1);
    smg.addObject(obj2);

    smg.addValue(val1);
    smg.addValue(val2);

    smg.addPointsToEdge(pt1to1);

    smg.addHasValueEdge(hv2has2at0);
    smg.addHasValueEdge(hv2has1at4);
  }

  @Test
  public void getNullBytesForObjectTest() {
    SMG smg = getNewSMG64();
    smg.addObject(obj1);
    SMGEdgeHasValue hv = new SMGEdgeHasValue(mockType, 32, obj1, smg.getNullValue());
    smg.addHasValueEdge(hv);

    TreeMap<Integer, Integer> nullEdges = smg.getNullEdgesMapOffsetToSizeForObject(obj1);
    assertThat(nullEdges).containsExactly(32, 32);
  }

  @Test
  public void replaceHVSetTest() {
    SMGEdgeHasValue hv = new SMGEdgeHasValue(mockType, 16, obj1, val1.intValue());
    Set<SMGEdgeHasValue> hvSet = new HashSet<>();
    hvSet.add(hv);

    smg.replaceHVSet(hvSet);

    Set<SMGEdgeHasValue> newHVSet = smg.getHVEdges();

    assertThat(newHVSet).isEqualTo(hvSet);
  }

  @Test
  public void SMGConstructorTest() {
    SMG smg = getNewSMG64();
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    SMGObject nullObject = smg.getNullObject();
    int nullAddress = smg.getNullValue();


    Assert.assertNotNull(nullObject);
    Assert.assertFalse(nullObject.notNull());
    assertThat(smg.getObjects()).hasSize(1);
    assertThat(smg.getObjects()).contains(nullObject);

    assertThat(smg.getValues()).hasSize(1);
    assertThat(smg.getValues()).contains(Integer.valueOf(nullAddress));

    assertThat(smg.getPTEdgesAsMap()).hasSize(1);
    SMGObject target_object = smg.getObjectPointedBy(nullAddress);
    Assert.assertEquals(nullObject, target_object);

    assertThat(smg.getHVEdges()).hasSize(0);

    //copy constructor
    SMG smg_copy = new SMG(smg);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg_copy));

    SMGObject third_object = new SMGRegion(128, "object-3");
    Integer third_value = Integer.valueOf(3);
    smg_copy.addObject(third_object);
    smg_copy.addValue(third_value);
    smg_copy.addHasValueEdge(new SMGEdgeHasValue(mockType, 0, third_object,  third_value));
    smg_copy.addPointsToEdge(new SMGEdgePointsTo(third_value, third_object, 0));

    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg_copy));
    assertThat(smg.getObjects()).hasSize(1);
    assertThat(smg_copy.getObjects()).hasSize(2);
    assertThat(smg_copy.getObjects()).contains(third_object);

    assertThat(smg.getValues()).hasSize(1);
    assertThat(smg_copy.getValues()).hasSize(2);
    assertThat(smg_copy.getValues()).contains(third_value);

    assertThat(smg.getPTEdges().asSet()).hasSize(1);
    assertThat(smg_copy.getPTEdges().asSet()).hasSize(2);
    SMGObject target_object_for_third = smg_copy.getObjectPointedBy(third_value);
    Assert.assertEquals(third_object, target_object_for_third);

    assertThat(smg.getHVEdges()).hasSize(0);
    assertThat(smg_copy.getHVEdges()).hasSize(1);
  }

  @Test
  public void addRemoveHasValueEdgeTest() {
    SMG smg = getNewSMG64();
    SMGObject object = new SMGRegion(32, "object");

    SMGEdgeHasValue hv = new SMGEdgeHasValue(mockType, 0, object, smg.getNullValue());

    smg.addHasValueEdge(hv);
    assertThat(smg.getHVEdges()).contains(hv);

    smg.removeHasValueEdge(hv);
    assertThat(smg.getHVEdges()).doesNotContain(hv);
  }

  @Test
  public void removeObjectTest() {
    SMG smg = getNewSMG64();
    Integer newValue = SMGValueFactory.getNewValue();

    SMGObject object = new SMGRegion(64, "object");
    SMGEdgeHasValue hv0 = new SMGEdgeHasValue(mockType, 0, object, 0);
    SMGEdgeHasValue hv4 = new SMGEdgeHasValue(mockType, 32, object, 0);
    SMGEdgePointsTo pt = new SMGEdgePointsTo(newValue, object, 0);

    smg.addValue(newValue);
    smg.addObject(object);
    smg.addPointsToEdge(pt);
    smg.addHasValueEdge(hv0);
    smg.addHasValueEdge(hv4);

    assertThat(smg.getObjects()).contains(object);
    smg.removeObject(object);
    assertThat(smg.getObjects()).doesNotContain(object);
    assertThat(smg.getHVEdges()).contains(hv0);
    assertThat(smg.getHVEdges()).contains(hv4);
    assertThat(smg.getPTEdges().asSet()).contains(pt);
  }

  @Test
  public void removeObjectAndEdgesTest() {
    SMG smg = getNewSMG64();
    Integer newValue = SMGValueFactory.getNewValue();

    SMGObject object = new SMGRegion(64, "object");
    SMGEdgeHasValue hv0 = new SMGEdgeHasValue(mockType, 0, object, 0);
    SMGEdgeHasValue hv4 = new SMGEdgeHasValue(mockType, 32, object, 0);
    SMGEdgePointsTo pt = new SMGEdgePointsTo(newValue, object, 0);

    smg.addValue(newValue);
    smg.addObject(object);
    smg.addPointsToEdge(pt);
    smg.addHasValueEdge(hv0);
    smg.addHasValueEdge(hv4);

    assertThat(smg.getObjects()).contains(object);
    smg.removeObjectAndEdges(object);
    assertThat(smg.getObjects()).doesNotContain(object);
    assertThat(smg.getHVEdges()).doesNotContain(hv0);
    assertThat(smg.getHVEdges()).doesNotContain(hv4);
    assertThat(smg.getPTEdges().asSet()).doesNotContain(pt);
  }

  @Test
  public void validityTest() {
    Assert.assertFalse(smg.isObjectValid(smg.getNullObject()));
    Assert.assertTrue(smg.isObjectValid(obj1));
    Assert.assertTrue(smg.isObjectValid(obj2));

    SMG smg_copy = new SMG(smg);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg_copy));
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.setValidity(obj1, false);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg_copy));
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    Assert.assertFalse(smg.isObjectValid(smg.getNullObject()));
    Assert.assertFalse(smg.isObjectValid(obj1));
    Assert.assertTrue(smg.isObjectValid(obj2));
    Assert.assertFalse(smg_copy.isObjectValid(smg_copy.getNullObject()));
    Assert.assertTrue(smg_copy.isObjectValid(obj1));
    Assert.assertTrue(smg_copy.isObjectValid(obj2));

    smg.setValidity(obj2, false);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg_copy));
    Assert.assertFalse(smg_copy.isObjectValid(smg_copy.getNullObject()));
    Assert.assertTrue(smg_copy.isObjectValid(obj1));
    Assert.assertTrue(smg_copy.isObjectValid(obj2));
  }

  @Test
  public void consistencyViolationValidNullTest() {
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.setValidity(smg.getNullObject(), true);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
  }

  @Test
  public void consistencyViolationInvalidRegionHasValueTest() {
    smg.setValidity(obj1, false);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.setValidity(obj2, false);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
  }

  @Test
  public void consistencyViolationFieldConsistency() {
    SMG smg1 = getNewSMG64();
    SMG smg2 = getNewSMG64();

    SMGObject object_2b = new SMGRegion(16, "object_2b");
    SMGObject object_4b = new SMGRegion(32, "object_4b");
    Integer random_value = Integer.valueOf(6);

    smg1.addObject(object_2b);
    smg2.addObject(object_4b);
    smg1.addValue(random_value);
    smg2.addValue(random_value);

    // Read 4 bytes (sizeof(mockType)) on offset 0 of 2b object -> out of bounds
    SMGEdgeHasValue invalidHV1 = new SMGEdgeHasValue(mockType, 0, object_2b, random_value);

    // Read 4 bytes (sizeof(mockType)) on offset 8 of 4b object -> out of bounds
    SMGEdgeHasValue invalidHV2 = new SMGEdgeHasValue(mockType, 64, object_4b, random_value);

    smg1.addHasValueEdge(invalidHV1);
    smg2.addHasValueEdge(invalidHV2);

    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg1));
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg2));
  }

  @Test
  public void consistencyViolationHVConsistency() {
    SMG smg = getNewSMG64();

    SMGObject object_8b = new SMGRegion(64, "object_8b");
    SMGObject object_16b = new SMGRegion(80, "object_10b");

    Integer first_value = Integer.valueOf(6);
    Integer second_value = Integer.valueOf(8);

    // 1, 3, 4 are consistent (different offsets or object)
    // 2 is inconsistent with 1 (same object and offset, different value)
    SMGEdgeHasValue hv_edge1 = new SMGEdgeHasValue(mockType, 0, object_8b, first_value);
    SMGEdgeHasValue hv_edge2 = new SMGEdgeHasValue(mockType, 0, object_8b, second_value);
    SMGEdgeHasValue hv_edge3 = new SMGEdgeHasValue(mockType, 32, object_8b, second_value);
    SMGEdgeHasValue hv_edge4 = new SMGEdgeHasValue(mockType, 0, object_16b, second_value);

    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addHasValueEdge(hv_edge1);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.addObject(object_8b);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.addValue(first_value);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addHasValueEdge(hv_edge3);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.addValue(second_value);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addHasValueEdge(hv_edge4);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
    smg.addObject(object_16b);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addHasValueEdge(hv_edge2);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
  }

  @Test
  public void consistencyViolationPTConsistency() {
    SMG smg = getNewSMG64();

    SMGObject object_8b = new SMGRegion(64, "object_8b");
    SMGObject object_16b = new SMGRegion(80, "object_10b");

    Integer first_value = Integer.valueOf(6);
    Integer second_value = Integer.valueOf(8);
    Integer third_value = Integer.valueOf(10);

    SMGEdgePointsTo edge1 = new SMGEdgePointsTo(first_value, object_8b, 0);
    SMGEdgePointsTo edge2 = new SMGEdgePointsTo(third_value, object_8b, 32);
    SMGEdgePointsTo edge3 = new SMGEdgePointsTo(second_value, object_16b, 0);
    SMGEdgePointsTo edge4 = new SMGEdgePointsTo(first_value, object_16b, 0);

    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addPointsToEdge(edge1);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addValue(first_value);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addObject(object_8b);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addPointsToEdge(edge2);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addValue(third_value);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addPointsToEdge(edge3);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addObject(object_16b);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addValue(second_value);
    Assert.assertTrue(SMGConsistencyVerifier.verifySMG(logger, smg));

    smg.addPointsToEdge(edge4);
    Assert.assertFalse(SMGConsistencyVerifier.verifySMG(logger, smg));
  }

  @Test(expected=IllegalArgumentException.class)
  public void isObjectValidBadCallTest() {
    smg.isObjectValid(new SMGRegion(192, "wee"));
  }

  @Test(expected=IllegalArgumentException.class)
  public void setValidityBadCallTest() {
    smg.setValidity(new SMGRegion(192, "wee"), true);
  }

  @Test
  public void getObjectsTest() {
    HashSet<SMGObject> set = new HashSet<>();
    set.add(obj1);
    set.add(obj2);
    set.add(smg.getNullObject());

    assertThat(smg.getObjects()).containsAllIn(set);
  }

  @Test
  public void getNullObjectTest() {
    SMGObject nullObject = smg.getNullObject();
    Assert.assertFalse(smg.isObjectValid(nullObject));
    assertThat(nullObject.getSize()).isEqualTo(0);
  }

  @Test
  public void getValuesTest() {
    HashSet<Integer> set = new HashSet<>();
    set.add(val1);
    set.add(val2);
    set.add(smg.getNullValue());

    assertThat(smg.getValues()).containsAllIn(set);
  }

  @Test
  public void getHVEdgesTest() {
    HashSet<SMGEdgeHasValue> set = new HashSet<>();
    set.add(hv2has2at0);
    set.add(hv2has1at4);

    assertThat(smg.getHVEdges()).containsAllIn(set);
  }

  @Test
  public void getPTEdgesTest() {
    HashSet<SMGEdgePointsTo> set = new HashSet<>();
    set.add(pt1to1);

    assertThat(smg.getPTEdges().asSet()).containsAllIn(set);
  }

  @Test
  public void getObjectPointedByTest() {
    assertThat(smg.getObjectPointedBy(val1)).isEqualTo(obj1);
    assertThat(smg.getObjectPointedBy(val2)).isNull();
  }

  @Test
  public void neqBasicTest() {
    NeqRelation nr = new NeqRelation();
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
    Assert.assertFalse(nr.neq_exists(two, one));
    Assert.assertFalse(nr.neq_exists(three, one));
    Assert.assertFalse(nr.neq_exists(three, two));

    nr.add_relation(one, three);

    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertTrue(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
    Assert.assertFalse(nr.neq_exists(two, one));
    Assert.assertTrue(nr.neq_exists(three, one));
    Assert.assertFalse(nr.neq_exists(three, two));

    nr.add_relation(one, three);

    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertTrue(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
    Assert.assertFalse(nr.neq_exists(two, one));
    Assert.assertTrue(nr.neq_exists(three, one));
    Assert.assertFalse(nr.neq_exists(three, two));

    nr.add_relation(two, three);

    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertTrue(nr.neq_exists(one, three));
    Assert.assertTrue(nr.neq_exists(two, three));
    Assert.assertFalse(nr.neq_exists(two, one));
    Assert.assertTrue(nr.neq_exists(three, one));
    Assert.assertTrue(nr.neq_exists(three, two));

    nr.remove_relation(one, three);

    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertTrue(nr.neq_exists(two, three));
    Assert.assertFalse(nr.neq_exists(two, one));
    Assert.assertFalse(nr.neq_exists(three, one));
    Assert.assertTrue(nr.neq_exists(three, two));
  }

  @Test
  public void neqPutAllTest() {
    NeqRelation nr = new NeqRelation();
    NeqRelation newNr = new NeqRelation();
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    nr.add_relation(one, three);

    newNr.putAll(nr);
    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertTrue(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
    Assert.assertFalse(newNr.neq_exists(two, one));
    Assert.assertTrue(newNr.neq_exists(three, one));
    Assert.assertFalse(newNr.neq_exists(three, two));

    nr.remove_relation(one, three);
    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
    Assert.assertFalse(newNr.neq_exists(two, one));
    Assert.assertTrue(newNr.neq_exists(three, one));
    Assert.assertFalse(newNr.neq_exists(three, two));
  }

  @Test
  public void neqRemoveValueTest() {
    NeqRelation nr = new NeqRelation();
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    nr.add_relation(one, two);
    nr.add_relation(one, three);
    nr.removeValue(one);
    Assert.assertFalse(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
  }

  @Test
  public void neqMergeValuesTest() {
    NeqRelation nr = new NeqRelation();
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    nr.add_relation(one, three);
    nr.mergeValues(two, three);

    Assert.assertTrue(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
  }

  @Test
  public void neqMergeValuesTest2() {
    NeqRelation nr = new NeqRelation();
    Integer zero = Integer.valueOf(0);
    Integer one = Integer.valueOf(1);
    Integer two = Integer.valueOf(2);
    Integer three = Integer.valueOf(3);

    nr.add_relation(zero, three);
    nr.add_relation(one, three);
    nr.mergeValues(two, three);

    Assert.assertTrue(nr.neq_exists(zero, two));
    Assert.assertTrue(nr.neq_exists(one, two));
    Assert.assertFalse(nr.neq_exists(zero, three));
    Assert.assertFalse(nr.neq_exists(one, three));
    Assert.assertFalse(nr.neq_exists(two, three));
  }
}
