/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.abiserver.business.hibernate.pojohb.workload.engine.core;

/**
 * In order to test some parts of ResourceAllocationService we created a 'for-test-only' class and
 * auxiliary resource, rule and target objects.
 * <p>
 * To make things simple, we have decided that we will create targets with a long value: if it is
 * greater than NO_PASS, then the target will be eligible. If it is bigger than or equal to
 * GOOD_ENOUGH_FIT, then that target will be selected immediately, else we will keep looking for new
 * targets, selected the biggest one (greater than GOOD_ENOUGH_FIT).
 */
public class ResourceAllocationServiceTest
{
    // private static final long NO_PASS = 49;
    // private static final long GOOD_ENOUGH_FIT = 100;
    //
    // private static class MyResource {
    //
    // }
    //
    // private static class MyTarget {
    // private long fit;
    // private MyTarget(long fit) {
    // this.fit = fit;
    // }
    // }
    //
    // private static class MyRule implements Rule<MyResource, MyTarget, Void> {
    //
    // @Override
    // public boolean pass(MyResource resource, MyTarget target, Void contextData)
    // {
    // return target.fit > NO_PASS;
    // }
    //
    // }
    //
    // private static class MyNoPassRule implements Rule<MyResource, MyTarget, Void> {
    //
    // @Override
    // public boolean pass(MyResource resource, MyTarget target, Void contextData)
    // {
    // return false;
    // }
    //
    // }
    //
    //
    // private static class MyResourceAllocationService extends
    // ResourceAllocationService<MyResource, MyTarget, Void> {
    //
    // private List<Rule<MyResource, MyTarget, Void>> rules;
    // private Collection<MyTarget> firstPassCandidates;
    //
    // protected MyResourceAllocationService(MyResource resource, FitPolicy fitPolicy,
    // List<Rule<MyResource, MyTarget, Void>> rules, Collection<MyTarget> firstPassCandidates)
    // {
    // super(resource, fitPolicy, null);
    // this.rules = rules;
    // this.firstPassCandidates = firstPassCandidates;
    // }
    //
    // @Override
    // protected long calculateFit(MyTarget target)
    // {
    // return target.fit;
    // }
    //
    // @Override
    // protected Collection<MyTarget> findFirstPassCandidates()
    // {
    // return firstPassCandidates;
    // }
    //
    // @Override
    // protected SecondPassRuleFinder<MyResource, MyTarget, Void> getRuleFinder()
    // {
    // return new SecondPassRuleFinder<MyResource, MyTarget, Void>()
    // {
    //
    // @Override
    // public List<Rule<MyResource, MyTarget, Void>> findRules(MyResource resource, MyTarget target,
    // Void contextData)
    // {
    // return rules;
    // }
    // };
    // }
    //
    // @Override
    // protected boolean isGoodEnough(long fitTarget)
    // {
    // return fitTarget >= GOOD_ENOUGH_FIT;
    // }
    //
    // }
    //
    // private static final List<MyTarget> createTargets( long ... values ) {
    // List<MyTarget> result = new ArrayList<MyTarget>();
    // for( long value : values) {
    // result.add( new MyTarget(value));
    // }
    // return result;
    // }
    //
    // @Test
    // public void test_findSecondPassCandidates() {
    // MyResource resource = new MyResource();
    // List<Rule<MyResource, MyTarget, Void>> rules = new ArrayList<Rule<MyResource, MyTarget,
    // Void>>();
    // rules.add( new MyRule());
    //
    // // No candidates => return null
    // MyResourceAllocationService service = new MyResourceAllocationService(resource,
    // FitPolicy.PERFORMANCE, rules, createTargets());
    // Assert.assertNull( service.findBestTarget() );
    //
    // // No candidate that passes the checks => return null
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 35, 48, 47));
    // Assert.assertNull( service.findBestTarget() );
    //
    // // Several candidates pass the check => return a target with fit = 99
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 98, 99));
    // Assert.assertEquals( service.findBestTarget().fit, 99 );
    //
    // // Only candidate at beginning passes the check => return a target with fit = 99
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(50, 49, 33, 20, 15));
    // Assert.assertEquals( service.findBestTarget().fit, 50 );
    //
    // // Only candidate at end passes the check => return a target with fit = 99
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(1, 49, 33, 20, 15, 50));
    // Assert.assertEquals( service.findBestTarget().fit, 50 );
    //
    // // Several candidates pass the check => return a target with fit = 99
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 99, 98));
    // Assert.assertEquals( service.findBestTarget().fit, 99 );
    //
    // // Several candidates pass the check, and there is one good enough that is not the best fit
    // => return the target with good enough fit (100)
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 100, 150));
    // Assert.assertEquals( service.findBestTarget().fit, 100 );
    //
    // // *************************************************
    // // *** Test things when there is a rule that will say 'no-pass'
    // rules.add( new MyNoPassRule());
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets());
    // Assert.assertNull( service.findBestTarget() );
    //
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 35, 48, 47));
    // Assert.assertNull( service.findBestTarget() );
    //
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 99, 99));
    // Assert.assertNull( service.findBestTarget() );
    //
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 99, 100));
    // Assert.assertNull( service.findBestTarget() );
    //
    // service = new MyResourceAllocationService(resource, FitPolicy.PERFORMANCE, rules,
    // createTargets(49, 50, 100, 150));
    // Assert.assertNull( service.findBestTarget() );
    // }
}
