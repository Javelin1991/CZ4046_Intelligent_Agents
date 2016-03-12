package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes.ActionUtilPair;
import classes.State;

public class FuncHelper {
	
	/**
	 * Copy the contents from the source array to the destination array
	 * 
	 * @param aSrc	Source array
	 * @param aDest	Destination array
	 */
	public static void array2DCopy(ActionUtilPair[][] aSrc, ActionUtilPair[][] aDest) {
		for (int i = 0; i < aSrc.length; i++) {
			System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
		}
	}
	
	/**
	 * Calculates the utility for each possible action<br>
	 * Returns the action with maximal utility
	 * 
	 * @param col			Column in the grid
	 * @param row			Row in the grid
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				An ActionUtilPair that contains the best action,
	 * 						and its corresponding utility
	 */
	public static ActionUtilPair calcNewUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		List<ActionUtilPair> lstActionUtilPairs = new ArrayList<>();

		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.UP,
				calcActionUpUtility(col, row, currUtilArr, grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.DOWN,
				calcActionDownUtility(col, row, currUtilArr, grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.LEFT,
				calcActionLeftUtility(col, row, currUtilArr, grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.RIGHT,
				calcActionRightUtility(col, row, currUtilArr, grid)));

		Collections.sort(lstActionUtilPairs);
		ActionUtilPair chosenActionUtilPair = lstActionUtilPairs.get(0);
		
		return chosenActionUtilPair;
	}
	
	/**
	 * Calculates the utility for attempting to move up
	 * 
	 * @param col			Column in the grid
	 * @param row			Row in the grid
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The utility for moving up
	 */
	public static double calcActionUpUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		double actionUpUtility = 0.000;
		
		// Intends to move up
		actionUpUtility += Const.PROB_INTENT * goUp(col, row, currUtilArr, grid);
		
		// Intends to move up, but goes right (CW) instead
		actionUpUtility += Const.PROB_CW * goRight(col, row, currUtilArr, grid);
		
		// Intends to move up, but goes left (CCW) instead
		actionUpUtility += Const.PROB_CCW * goLeft(col, row, currUtilArr, grid);
		
		// Final utility
		actionUpUtility = grid[col][row].getReward() + Const.DISCOUNT * actionUpUtility;
		
		return actionUpUtility;
	}
	
	/**
	 * Calculates the utility for attempting to move down
	 * 
	 * @param col			Column in the grid
	 * @param row			Row in the grid
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The utility for moving down
	 */
	public static double calcActionDownUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		double actionDownUtility = 0.000;
		
		// Intends to move down
		actionDownUtility += Const.PROB_INTENT * goDown(col, row, currUtilArr, grid);
		
		// Intends to move down, but goes left (CW) instead
		actionDownUtility += Const.PROB_CW * goLeft(col, row, currUtilArr, grid);
		
		// Intends to move down, but goes right (CCW) instead
		actionDownUtility += Const.PROB_CCW * goRight(col, row, currUtilArr, grid);
		
		// Final utility
		actionDownUtility = grid[col][row].getReward() + Const.DISCOUNT * actionDownUtility;
		
		return actionDownUtility;
	}
	
	/**
	 * Calculates the utility for attempting to move left
	 * 
	 * @param col			Column in the grid
	 * @param row			Row in the grid
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The utility for moving left
	 */
	public static double calcActionLeftUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		double actionLeftUtility = 0.000;
		
		// Intends to move left
		actionLeftUtility += Const.PROB_INTENT * goLeft(col, row, currUtilArr, grid);
		
		// Intends to move left, but goes up (CW) instead
		actionLeftUtility += Const.PROB_CW * goUp(col, row, currUtilArr, grid);
		
		// Intends to move left, but goes down (CCW) instead
		actionLeftUtility += Const.PROB_CCW * goDown(col, row, currUtilArr, grid);
		
		// Final utility
		actionLeftUtility = grid[col][row].getReward() + Const.DISCOUNT * actionLeftUtility;
		
		return actionLeftUtility;
	}
	
	/**
	 * Calculates the utility for attempting to move right
	 * 
	 * @param col			Column in the grid
	 * @param row			Row in the grid
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The utility for moving right
	 */
	public static double calcActionRightUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		double actionRightUtility = 0.000;
		
		// Intends to move right
		actionRightUtility += Const.PROB_INTENT * goRight(col, row, currUtilArr, grid);
		
		// Intends to move right, but goes down (CW) instead
		actionRightUtility += Const.PROB_CW * goDown(col, row, currUtilArr, grid);
		
		// Intends to move right, but goes up (CCW) instead
		actionRightUtility += Const.PROB_CCW * goUp(col, row, currUtilArr, grid);
		
		// Final utility
		actionRightUtility = grid[col][row].getReward() + Const.DISCOUNT * actionRightUtility;
		
		return actionRightUtility;
	}
	
	/**
	 * Attempts to go up<br>
	 * Succeeds if the target state is not out-of-bounds and not a wall<br>
	 * Failure results in the agent staying in the same place as before<br>
	 * Returns the utility value of the resulting state
	 * 
	 * @param col			Current column
	 * @param row			Current row
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goUp(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		return (row - 1 >= 0 && !grid[col][row - 1].isWall()) ?
				currUtilArr[col][row - 1].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	/**
	 * Attempts to go down<br>
	 * Succeeds if the target state is not out-of-bounds and not a wall<br>
	 * Failure results in the agent staying in the same place as before<br>
	 * Returns the utility value of the resulting state
	 * 
	 * @param col			Current column
	 * @param row			Current row
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goDown(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		return (row + 1 < Const.NUM_ROWS && !grid[col][row + 1].isWall()) ?
				currUtilArr[col][row + 1].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	/**
	 * Attempts to go left<br>
	 * Succeeds if the target state is not out-of-bounds and not a wall<br>
	 * Failure results in the agent staying in the same place as before<br>
	 * Returns the utility value of the resulting state
	 * 
	 * @param col			Current column
	 * @param row			Current row
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goLeft(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		return (col - 1 >= 0 && !grid[col - 1][row].isWall()) ?
				currUtilArr[col - 1][row].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	/**
	 * Attempts to go right<br>
	 * Succeeds if the target state is not out-of-bounds and not a wall<br>
	 * Failure results in the agent staying in the same place as before<br>
	 * Returns the utility value of the resulting state
	 * 
	 * @param col			Current column
	 * @param row			Current row
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goRight(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] grid) {
		
		return (col + 1 < Const.NUM_COLS && !grid[col + 1][row].isWall()) ?
				currUtilArr[col + 1][row].getUtil() : currUtilArr[col][row].getUtil();
	}
}
