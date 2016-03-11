package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes.ActionUtilPair;
import classes.GridWorld;
import classes.State;
import util.ActionUtilHelper;
import util.Const;

public class ValueIterationApp {

	public static GridWorld _gridWorld = null;
	
	public static void main(String[] args) {
		
		_gridWorld = new GridWorld();
		
		// Displays the Grid World, just for debugging purposes to ensure correctness
		_gridWorld.displayGrid();
		
		State[][] _grid = _gridWorld.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + Const.DISCOUNT);
		System.out.println("Rmax: " + Const.R_MAX);
		System.out.println("Constant 'c': " + Const.C);
		System.out.println("Epsilon (c * Rmax): " + Const.EPSILON);
		
		// Perform value iteration
		List<ActionUtilPair[][]> lstActionUtilPairs = valueIteration(_grid);
		
		// Final item in the list is the optimal policy derived by value iteration
		final ActionUtilPair[][] optimalPolicy =
				lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);
		
		// Display the utilities of all the (non-wall) states
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				if (!_grid[col][row].isWall()) {
					System.out.printf("(%1d, %1d): %-2.6f%n", col, row,
							optimalPolicy[col][row].getUtil());
				}
			}
		}
		
		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		ActionUtilHelper.displayPolicy(optimalPolicy);
		
		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		ActionUtilHelper.displayUtilities(optimalPolicy);
	}
	
	public static List<ActionUtilPair[][]> valueIteration(final State[][] _grid) {
		
		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}
		
		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();
		
		double delta = 0.000;
		double convergenceCriteria =
				Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);
		System.out.printf("Convergence criteria: %.5f "
				+ "(i.e. delta must be < this value)%n", convergenceCriteria);
		int numIterations = 0;
		
		do {
			
			array2DCopy(newUtilArr, currUtilArr);
			delta = 0.000;
			
			// Append to list of ActionUtilPair
			lstActionUtilPairs.add(currUtilArr);
			
			// For each state
			for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
		        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
		        	
		        	// Not necessary to calculate for walls
		        	if(_grid[col][row].isWall())
		        		continue;
		        	
		        	newUtilArr[col][row] = calcNewUtility(col, row, currUtilArr, _grid);
		        	
		        	// Update maximum change (delta) if necessary
		        	double newUtil = newUtilArr[col][row].getUtil();
		        	double currUtil = currUtilArr[col][row].getUtil();
		        	double sDelta = Math.abs(newUtil - currUtil);
		        	if(sDelta > delta) {
		        		delta = sDelta;
		        	}
		        }
			}
			
			// Print newUtilityArr?
			numIterations++;
			
		} while (delta >= convergenceCriteria);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return lstActionUtilPairs;
	}
	
	public static void array2DCopy(ActionUtilPair[][] aSrc, ActionUtilPair[][] aDest) {
	    for (int i = 0; i < aSrc.length; i++) {
	        System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
	    }
	}
	
	public static ActionUtilPair calcNewUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		List<ActionUtilPair> lstActionUtilPairs = new ArrayList<>();

		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.UP,
				calcActionUpUtility(col, row, currUtilArr, _grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.DOWN,
				calcActionDownUtility(col, row, currUtilArr, _grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.LEFT,
				calcActionLeftUtility(col, row, currUtilArr, _grid)));
		lstActionUtilPairs.add(new ActionUtilPair(ActionUtilPair.Action.RIGHT,
				calcActionRightUtility(col, row, currUtilArr, _grid)));

		Collections.sort(lstActionUtilPairs);

		ActionUtilPair chosenActionUtilPair = lstActionUtilPairs.get(0);
		chosenActionUtilPair.setUtil(_grid[col][row].getReward()
				+ Const.DISCOUNT * chosenActionUtilPair.getUtil());
		
		return chosenActionUtilPair;
	}
	
	public static double calcActionUpUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		double actionUpUtility = 0.000;
		
		// Intends to move up
		actionUpUtility += Const.PROB_INTENT * goUp(col, row, currUtilArr, _grid);
		
		// Intends to move up, but goes right (CW) instead
		actionUpUtility += Const.PROB_CW * goRight(col, row, currUtilArr, _grid);
		
		// Intends to move up, but goes left (CCW) instead
		actionUpUtility += Const.PROB_CCW * goLeft(col, row, currUtilArr, _grid);
		
		return actionUpUtility;
	}
	
	public static double calcActionDownUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		double actionDownUtility = 0.000;
		
		// Intends to move down
		actionDownUtility += Const.PROB_INTENT * goDown(col, row, currUtilArr, _grid);
		
		// Intends to move down, but goes left (CW) instead
		actionDownUtility += Const.PROB_CW * goLeft(col, row, currUtilArr, _grid);
		
		// Intends to move down, but goes right (CCW) instead
		actionDownUtility += Const.PROB_CCW * goRight(col, row, currUtilArr, _grid);
		
		return actionDownUtility;
	}
	
	public static double calcActionLeftUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		double actionLeftUtility = 0.000;
		
		// Intends to move left
		actionLeftUtility += Const.PROB_INTENT * goLeft(col, row, currUtilArr, _grid);
		
		// Intends to move left, but goes up (CW) instead
		actionLeftUtility += Const.PROB_CW * goUp(col, row, currUtilArr, _grid);
		
		// Intends to move left, but goes down (CCW) instead
		actionLeftUtility += Const.PROB_CCW * goDown(col, row, currUtilArr, _grid);
		
		return actionLeftUtility;
	}
	
	public static double calcActionRightUtility(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		double actionRightUtility = 0.000;
		
		// Intends to move right
		actionRightUtility += Const.PROB_INTENT * goRight(col, row, currUtilArr, _grid);
		
		// Intends to move right, but goes down (CW) instead
		actionRightUtility += Const.PROB_CW * goDown(col, row, currUtilArr, _grid);
		
		// Intends to move right, but goes up (CCW) instead
		actionRightUtility += Const.PROB_CCW * goUp(col, row, currUtilArr, _grid);
		
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
	 * @param _grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goUp(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		return (row - 1 >= 0 && !_grid[col][row - 1].isWall()) ?
				currUtilArr[col][row - 1].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	public static double goDown(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		return (row + 1 < Const.NUM_ROWS && !_grid[col][row + 1].isWall()) ?
				currUtilArr[col][row + 1].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	public static double goLeft(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		return (col - 1 >= 0 && !_grid[col - 1][row].isWall()) ?
				currUtilArr[col - 1][row].getUtil() : currUtilArr[col][row].getUtil();
	}
	
	public static double goRight(final int col, final int row,
			final ActionUtilPair[][] currUtilArr, final State[][] _grid) {
		
		return (col + 1 < Const.NUM_COLS && !_grid[col + 1][row].isWall()) ?
				currUtilArr[col + 1][row].getUtil() : currUtilArr[col][row].getUtil();
	}
}
