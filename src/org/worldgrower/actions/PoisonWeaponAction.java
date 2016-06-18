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
package org.worldgrower.actions;

import java.io.ObjectStreamException;

import org.worldgrower.Constants;
import org.worldgrower.World;
import org.worldgrower.WorldObject;
import org.worldgrower.attribute.WorldObjectContainer;
import org.worldgrower.goal.WaterPropertyUtils;
import org.worldgrower.gui.ImageIds;
import org.worldgrower.gui.music.SoundIds;

public class PoisonWeaponAction extends InventoryAction {

	@Override
	public void execute(WorldObject performer, WorldObject target, int[] args, World world) {
		int inventoryIndex = args[0];
		WorldObject weaponTarget = performer.getProperty(Constants.INVENTORY).get(inventoryIndex);
		
		WaterPropertyUtils.poisonWaterSource(performer, weaponTarget, args, world);
	}
	
	@Override
	public boolean isValidInventoryItem(WorldObject inventoryItem, WorldObjectContainer inventory, WorldObject performer) {
		return inventoryItem.hasProperty(Constants.DAMAGE);
	}
	
	@Override
	public int distanceToInventoryItem(WorldObject inventoryItem, WorldObjectContainer inventory, WorldObject performer) {
		boolean inventoryContainsPoison = inventory.getQuantityFor(Constants.POISON_DAMAGE) > 0;
		return inventoryContainsPoison ? 0 : 1;
	}
	
	@Override
	public String getRequirementsDescription() {
		return CraftUtils.getRequirementsDescription(Constants.DAMAGE, 1);
	}

	@Override
	public String getDescription(WorldObject performer, WorldObject target, int[] args, World world) {
		return "poisoning weapon";
	}

	@Override
	public String getSimpleDescription() {
		return "poison weapon";
	}
	
	public Object readResolve() throws ObjectStreamException {
		return readResolveImpl();
	}
	
	@Override
	public ImageIds getImageIds() {
		return ImageIds.POISONED_INDICATOR;
	}
	
	@Override
	public SoundIds getSoundId() {
		return SoundIds.POISON;
	}
}