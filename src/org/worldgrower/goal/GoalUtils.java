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
package org.worldgrower.goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.worldgrower.Constants;
import org.worldgrower.CreaturePositionCondition;
import org.worldgrower.ManagedOperation;
import org.worldgrower.OperationInfo;
import org.worldgrower.Reach;
import org.worldgrower.World;
import org.worldgrower.WorldObject;
import org.worldgrower.attribute.ManagedProperty;

public class GoalUtils {

	private static class WorldObjectDistanceComparator implements Comparator<WorldObject> {
		private final WorldObject performer;
		
		public WorldObjectDistanceComparator(WorldObject performer) {
			super();
			this.performer = performer;
		}

		@Override
		public int compare(WorldObject o1, WorldObject o2) {
			return Integer.compare(Reach.distance(performer, o1), Reach.distance(performer, o2));
		}
	}

	public static WorldObject findNearestTarget(WorldObject performer, ManagedOperation action, World world) {
		List<WorldObject> worldObjects = world.findWorldObjects(w -> action.isValidTarget(performer, w, world));
		
		if (worldObjects.size() > 0) {
			WorldObject closestWorldObject = null;
			int closestDistance = Integer.MAX_VALUE;
			for(WorldObject worldObject : worldObjects) {
				int distance = Reach.distance(performer, worldObject);
				if (distance < closestDistance) {
					closestWorldObject = worldObject;
					closestDistance = distance;
				}
			}

			return closestWorldObject;
		} else {
			return null;
		}
	}
	
	public static List<WorldObject> findNearestTargets(WorldObject performer, ManagedOperation action, Predicate<WorldObject> condition, World world) {
		List<WorldObject> targets =  world.findWorldObjects(w -> action.isValidTarget(performer, w, world) && condition.test(w));
		Collections.sort(targets, new WorldObjectDistanceComparator(performer));
		return targets;
	}
	
	public static List<WorldObject> findNearestTargetsByProperty(WorldObject performer, ManagedOperation action, ManagedProperty<?> property, Predicate<WorldObject> condition, World world) {
		List<WorldObject> targets = world.findWorldObjectsByProperty(property, w -> action.isValidTarget(performer, w, world) && condition.test(w));
		Collections.sort(targets, new WorldObjectDistanceComparator(performer));
		return targets;
	}
	
	public static OperationInfo createOperationInfo(WorldObject performer, ManagedOperation action, int[] args, World world) {
		List<WorldObject> targets = findNearestTargets(performer, action, w -> world.getHistory().findHistoryItem(performer, w, args, action) == null, world);
		if (targets.size() > 0) {
			return new OperationInfo(performer, targets.get(0), args, action);
		}
		return null;
	}
	
	public static int[] findOpenSpace(WorldObject performer, int width, int height, World world) {
		int performerX = performer.getProperty(Constants.X);
		int performerY = performer.getProperty(Constants.Y);
		for(int x=-width; x<=width; x++) {
			for(int y=-height; y<=height; y++) {
				if (!LocationUtils.areInvalidCoordinates(x, y, world)) {
					int openSpaceX = performerX + x;
					int openSpaceY = performerY + y;
					
					if (isOpenSpace(openSpaceX, openSpaceY, width, height, world)) {
						return new int[] { x, y };
					}
				}
			}
		}
		return null;
		//throw new IllegalStateException("Performer " + performer + " can't find open space");
	}
	
	public static boolean isOpenSpace(int openSpaceX, int openSpaceY, int width, int height, World world) {
		for(int x=openSpaceX; x<openSpaceX+width; x++) {
			for(int y=openSpaceY; y<openSpaceY+height; y++) {
				if (LocationUtils.areInvalidCoordinates(x, y, world)) {
					return false;
				} else {
					if (LocationPropertyUtils.getWorldObjects(x, y, world).size() > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean actionAlreadyPerformed(WorldObject performer, WorldObject target, ManagedOperation action, int[] args, World world) {
		List<WorldObject> targets = findNearestNewTargets(performer, action, args, world);
		return !targets.contains(target);
	}

	public static List<WorldObject> findNearestNewTargets(WorldObject performer, ManagedOperation action, int[] args, World world) {
		return GoalUtils.findNearestTargets(performer, action, w -> world.getHistory().findHistoryItem(performer, w, args, action) == null && !performer.equals(w), world);
	}
	
	public static List<WorldObject> findNearestNewTargets(WorldObject performer, ManagedOperation action, int[] args, Predicate<WorldObject> condition, World world) {
		return GoalUtils.findNearestTargets(performer, action, w -> !performer.equals(w) && condition.test(w) && world.getHistory().findHistoryItem(performer, w, args, action) == null, world);
	}

	public static boolean canEnlarge(WorldObject target, World world) {
		int newHeight = target.getProperty(Constants.HEIGHT) * 2;
		int newWidth = target.getProperty(Constants.WIDTH) * 2;
		
		int openSpaceX = target.getProperty(Constants.X);
		int openSpaceY = target.getProperty(Constants.Y);
		
		List<WorldObject> allFoundWorldObjects = new ArrayList<>();
		for(int x=openSpaceX; x<openSpaceX+newWidth; x++) {
			for(int y=openSpaceY; y<openSpaceY+newHeight; y++) {
				List<WorldObject> foundWorldObjects = world.findWorldObjects(new CreaturePositionCondition(y, x));
				for(WorldObject foundWorldObject : foundWorldObjects) {
					if (!allFoundWorldObjects.contains(foundWorldObject)) {
						allFoundWorldObjects.add(foundWorldObject);
					}
				}
			}
		}
		if (allFoundWorldObjects.size() > 1) {
			return false;
		} else if (allFoundWorldObjects.size() == 1) {
			return allFoundWorldObjects.get(0).equals(target);
		} else {
			return true;
		}
		
	}
	
	public static boolean currentGoalHasLowerPriorityThan(Goal cutOffGoal, WorldObject target, World world) {
		Goal targetGoal = world.getGoal(target);
		List<Goal> goals = target.getPriorities(world);
		int indexOfGoal = goals.indexOf(targetGoal);
		int indexOfRestGoal = goals.indexOf(cutOffGoal);
		return (indexOfGoal > indexOfRestGoal);
	}
}
