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

import org.junit.Test;
import org.worldgrower.Constants;
import org.worldgrower.MockTerrain;
import org.worldgrower.MockWorld;
import org.worldgrower.TestUtils;
import org.worldgrower.World;
import org.worldgrower.WorldImpl;
import org.worldgrower.WorldObject;
import org.worldgrower.actions.Actions;
import org.worldgrower.condition.Conditions;
import org.worldgrower.terrain.TerrainType;

public class UTestLightningBoltAttackAction {

	@Test
	public void testExecute() {
		World world = new MockWorld(new MockTerrain(TerrainType.GRASLAND), new WorldImpl(10, 10, null, null));
		WorldObject performer = createPerformer(2);
		WorldObject target = createPerformer(3);
		
		target.setProperty(Constants.HIT_POINTS, 10);
		target.setProperty(Constants.HIT_POINTS_MAX, 10);
		
		Actions.LIGHTNING_BOLT_ATTACK_ACTION.execute(performer, target, new int[0], world);
		
		assertEquals(5, target.getProperty(Constants.HIT_POINTS).intValue());
	}
	
	@Test
	public void testExecuteWater() {
		World world = new MockWorld(new MockTerrain(TerrainType.WATER), new WorldImpl(10, 10, null, null));
		WorldObject performer = createPerformer(2);
		WorldObject target = createPerformer(3);
		
		performer.setProperty(Constants.HIT_POINTS, 10);
		performer.setProperty(Constants.HIT_POINTS_MAX, 10);
		
		target.setProperty(Constants.HIT_POINTS, 10);
		target.setProperty(Constants.HIT_POINTS_MAX, 10);
		
		world.addWorldObject(performer);
		world.addWorldObject(target);
		
		Actions.LIGHTNING_BOLT_ATTACK_ACTION.execute(performer, target, new int[0], world);
		
		assertEquals(5, performer.getProperty(Constants.HIT_POINTS).intValue());
		assertEquals(0, target.getProperty(Constants.HIT_POINTS).intValue());
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