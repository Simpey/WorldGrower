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
package org.worldgrower.gui;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.worldgrower.Constants;
import org.worldgrower.TestUtils;
import org.worldgrower.World;
import org.worldgrower.WorldImpl;
import org.worldgrower.WorldObject;
import org.worldgrower.generator.BuildingGenerator;
import org.worldgrower.generator.CreatureGenerator;
import org.worldgrower.goal.GroupPropertyUtils;

public class UTestBonusDescriptions {

	private BonusDescriptions bonusDescriptions = new BonusDescriptions();
	
	@Test
	public void testGetCattleDescription() throws IOException {
		World world = new WorldImpl(1, 1, null, null);
		WorldObject cow = generateCow(world);
		assertEquals("Cow(meat: 1)", bonusDescriptions.getWorldObjectDescription(cow, world));
		
		WorldObject performer = TestUtils.createWorldObject(6, "Test1");
		world.addWorldObject(performer);
		cow.setProperty(Constants.CATTLE_OWNER_ID, performer.getProperty(Constants.ID));
		assertEquals("Cow (owner: Test1, meat: 1)", bonusDescriptions.getWorldObjectDescription(cow, world));
	}
	
	@Test
	public void testGetHouseDescription() throws IOException {
		World world = new WorldImpl(10, 10, null, null);
		WorldObject performer = TestUtils.createSkilledWorldObject(6);
		world.addWorldObject(performer);
		WorldObject house = generateHouse(performer, world);
		assertEquals("worldObject's house (sleep bonus: 6)", bonusDescriptions.getWorldObjectDescription(house, world));
	}
	
	private WorldObject generateCow(World world) {
		int cowId = new CreatureGenerator(GroupPropertyUtils.create(null, "TestOrg", world)).generateCow(0, 0, world);
		WorldObject cow = world.findWorldObjectById(cowId);
		return cow;
	}
	
	private WorldObject generateHouse(WorldObject owner, World world) {
		int id = BuildingGenerator.generateHouse(0, 0, world, owner);
		return world.findWorldObjectById(id);
	}
}
