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
package org.worldgrower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.worldgrower.actions.Actions;
import org.worldgrower.condition.CreatureTypeChangedListeners;
import org.worldgrower.goal.Goal;
import org.worldgrower.history.HistoryItem;

/**
 * The DungeonMaster class is responsible for executing actions, making sure they can be executed and making npc's
 * choose the correct actions.
 */
public class DungeonMaster implements Serializable {
	
	private final GoalCalculator goalCalculator = new GoalCalculator();
	private TaskCalculator taskCalculator = new TaskCalculatorImpl();
	
	public void runWorld(World world, CreatureTypeChangedListeners creatureTypeChangedListeners) {
		List<WorldObject> worldObjects = new ArrayList<>(world.getWorldObjects());
		
		for(WorldObject worldObject : worldObjects) {
			if (world.exists(worldObject)) {
				if (worldObject.hasIntelligence() && worldObject.isControlledByAI()) {
					runWorldObject(worldObject, world);
				}			
				worldObject.onTurn(world, creatureTypeChangedListeners);
			}
		}
		world.nextTurn();
	}

	void runWorldObject(WorldObject worldObject, World world) {
		MetaInformation metaInformation = getMetaInformation(worldObject);
		
		World worldFacade = createWorldFacade(worldObject, world);
		
		if (metaInformation.isEmpty()) {
			calculateGoalAndTasks(worldObject, worldFacade, metaInformation, GoalChangedReason.EMPTY_META_INFORMATION);
		} else {
			Goal finalGoal = metaInformation.getFinalGoal();
			if (finalGoal.isGoalMet(worldObject, worldFacade)) {
				calculateGoalAndTasks(worldObject, worldFacade, metaInformation, GoalChangedReason.FINAL_GOAL_WAS_MET);
			}
		}

		if (!metaInformation.getImmediateGoal().isValidTarget(worldFacade) || moreUrgentImportantGoalIsNotMet(worldObject, worldFacade)) {
			calculateGoalAndTasks(worldObject, worldFacade, metaInformation, GoalChangedReason.TARGET_NO_LONGER_VALID_OR_MORE_IMPORTANT_GOAL_NOT_MET);
		}
		
		OperationInfo peekOperationInfo = metaInformation.getCurrentTask().peek();
		if (!peekOperationInfo.isPossible(worldObject, worldFacade)) {
			recalculateTasks(worldObject, worldFacade, metaInformation, GoalChangedReason.OPERATION_NOT_POSSIBLE);
		}
		if (peekOperationInfo.targetMoved(worldFacade)) {
			recalculateTasks(worldObject, worldFacade, metaInformation, GoalChangedReason.TARGET_MOVED);
		}
		
		if (isDeceivedByWorldFacade(metaInformation.getCurrentTask().peek(), worldObject, world, worldFacade)) {
			recalculateTasks(worldObject, world, metaInformation, GoalChangedReason.DECEIVED);
		}
		
		OperationInfo operationInfo = metaInformation.getCurrentTask().poll();
		operationInfo.perform(world);
	}
	
	private boolean isDeceivedByWorldFacade(OperationInfo operationInfo, WorldObject worldObject, World world, World worldFacade) {
		boolean isPossibleInFacadeWorld = operationInfo.isPossible(worldObject, worldFacade);
		boolean isPossibleInRealWorld = operationInfo.isPossible(worldObject, world);
		
		if (isPossibleInFacadeWorld && !isPossibleInRealWorld) {
			return true;
		} else {
			return false;
		}
	}

	private World createWorldFacade(WorldObject worldObject, World world) {
		return new WorldFacade(worldObject, world);
	}

	private boolean moreUrgentImportantGoalIsNotMet(WorldObject worldObject, World world) {
		Goal currentGoal = getMetaInformation(worldObject).getFinalGoal();
		return goalCalculator.moreUrgentImportantGoalIsNotMet(worldObject, world, currentGoal);
	}

	private MetaInformation getMetaInformation(WorldObject worldObject) {
		MetaInformation metaInformation = worldObject.getProperty(Constants.META_INFORMATION);
		if (metaInformation == null) {
			metaInformation = new MetaInformation(worldObject);
			worldObject.setProperty(Constants.META_INFORMATION, metaInformation);
		}
		return metaInformation;
	}

	private void calculateGoalAndTasks(WorldObject worldObject, World world, MetaInformation metaInformation, GoalChangedReason goalChangedReason) {
		boolean goalFound = false;
		List<Goal> triedGoals = new ArrayList<>();
		
		while (!goalFound) {
			GoalAndOperationInfo goalAndOperationInfo = goalCalculator.calculateGoal(worldObject, world, triedGoals);
			Goal finalGoal = goalAndOperationInfo.getGoal();
			List<OperationInfo> tasks = calculateTasks(worldObject, world, goalAndOperationInfo.getOperationInfo());
			if (tasks.size() > 0) {
				metaInformation.setCurrentTask(tasks, goalChangedReason);
				metaInformation.setFinalGoal(finalGoal);
				goalFound = true;
				//	System.out.println(worldObject.getProperty(Constants.NAME) + " : final goal : " + finalGoal + " , immediateGoal : " + immediateGoal);
			} else {
				if (triedGoals.contains(finalGoal)) {
					throw new IllegalStateException("TriedGoals " + triedGoals + " already containd goal " + finalGoal + " for performer " + worldObject);
				}
				triedGoals.add(finalGoal);
			}
		}
	}

	private void recalculateTasks(WorldObject worldObject, World world, MetaInformation metaInformation, GoalChangedReason goalChangedReason) {
		if (metaInformation.isEmpty()) {
			throw new IllegalStateException("WorldObject " + worldObject + " has no goal");
		}
		
		List<OperationInfo> tasks = calculateTasks(worldObject, world, metaInformation.getImmediateGoal());
		if (tasks.size() == 0) {
			// for now, try another goal
			calculateGoalAndTasks(worldObject, world, metaInformation, goalChangedReason);
		} else {
			metaInformation.setCurrentTask(tasks, goalChangedReason);
		}
	}
	
	List<OperationInfo> calculateTasks(WorldObject worldObject, World world, OperationInfo immediateGoal) {
		if (worldObject.canWorldObjectPerformAction(Actions.MOVE_ACTION)) {
			return taskCalculator.calculateTask(worldObject, world, immediateGoal);
		} else {
			if (immediateGoal.isPossible(worldObject, world)) {
				List<OperationInfo> tasks = Arrays.asList(immediateGoal);
				return tasks;
			} else {
				return new ArrayList<>();
			}
		}
	}

	public void executeAction(ManagedOperation action, WorldObject performer, WorldObject target, int[] args, World world) {
		new OperationInfo(performer, target, args, action).perform(world);
	}
	
	public Goal getGoal(WorldObject worldObject) {
		return getMetaInformation(worldObject).getFinalGoal();
	}

	public OperationInfo getImmediateGoal(WorldObject worldObject, World world) {
		MetaInformation metaInformation = getMetaInformation(worldObject);
		if (!metaInformation.isEmpty()) {
			return metaInformation.getImmediateGoal();
		} else {
			HistoryItem historyItem = world.getHistory().getLastPerformedOperation(worldObject);
			if (historyItem != null) {
				return historyItem.getOperationInfo();
			} else {
				return null;	
			}
		}
	}
	
	void setTaskCalculator(TaskCalculator taskCalculator) {
		this.taskCalculator = taskCalculator;
	}
}