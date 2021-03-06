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

import org.worldgrower.condition.Condition;
import org.worldgrower.gui.ImageIds;

public class ProtectionFromIceAction extends AbstractProtectionFromEnergyAction {
	
	@Override
	public ImageIds getImageIds() {
		return ImageIds.PROTECTION_FROM_ICE;
	}

	@Override
	public ImageIds getAnimationImageId() {
		return ImageIds.PROTECTION_FROM_ICE_ANIMATION;
	}

	@Override
	public Condition getCondition() {
		return Condition.PROTECTION_FROM_ICE_CONDITION;
	}
	
	public String getEnergyDescription() {
		return "ice";
	}
}
