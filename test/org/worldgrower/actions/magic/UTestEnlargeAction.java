/*******************************************************************************
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.worldgrower.actions.magic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.worldgrower.Args;
import org.worldgrower.Constants;
import org.worldgrower.TestUtils;
import org.worldgrower.World;
import org.worldgrower.WorldImpl;
import org.worldgrower.WorldObject;
import org.worldgrower.actions.Actions;
import org.worldgrower.condition.Condition;
import org.worldgrower.condition.Conditions;

public class UTestEnlargeAction {

	@Test
	public void testExecute() {
		World world = new WorldImpl(10, 10, null, null);
		WorldObject performer = createPerformer(2);
		
		Actions.ENLARGE_ACTION.execute(performer, performer, Args.EMPTY, world);
		
		assertEquals(true, performer.getProperty(Constants.CONDITIONS).hasCondition(Condition.ENLARGED_CONDITION));
	}

	@Test
	public void testExecuteReduced() {
		World world = new WorldImpl(10, 10, null, null);
		WorldObject performer = createPerformer(2);
		
		Actions.REDUCE_ACTION.execute(performer, performer, Args.EMPTY, world);
		
		Actions.ENLARGE_ACTION.execute(performer, performer, Args.EMPTY, world);
		
		assertEquals(false, performer.getProperty(Constants.CONDITIONS).hasCondition(Condition.ENLARGED_CONDITION));
		assertEquals(false, performer.getProperty(Constants.CONDITIONS).hasCondition(Condition.REDUCED_CONDITION));
	}
	
	@Test
	public void testIsValidTarget() {
		World world = new WorldImpl(1, 1, null, null);
		WorldObject performer = createPerformer(2);
		performer.setProperty(Constants.KNOWN_SPELLS, Arrays.asList(Actions.ENLARGE_ACTION));
		
		assertEquals(true, Actions.ENLARGE_ACTION.isValidTarget(performer, performer, world));
	}
	
	@Test
	public void testIsActionPossible() {
		World world = new WorldImpl(2, 2, null, null);
		WorldObject performer = createPerformer(2);
		
		assertEquals(true, Actions.ENLARGE_ACTION.isActionPossible(performer, performer, Args.EMPTY, world));
	}
	
	@Test
	public void testDistance() {
		World world = new WorldImpl(1, 1, null, null);
		WorldObject performer = createPerformer(2);
		WorldObject target = createPerformer(3);
		
		assertEquals(0, Actions.ENLARGE_ACTION.distance(performer, target, Args.EMPTY, world));
	}
	
	private WorldObject createPerformer(int id) {
		WorldObject performer = TestUtils.createSkilledWorldObject(id, Constants.CONDITIONS, new Conditions());
		performer.setProperty(Constants.X, 0);
		performer.setProperty(Constants.Y, 0);
		performer.setProperty(Constants.WIDTH, 1);
		performer.setProperty(Constants.HEIGHT, 1);
		performer.setProperty(Constants.ENERGY, 1000);
		return performer;
	}
}