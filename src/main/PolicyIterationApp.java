package main;

import java.util.ArrayList;
import java.util.List;

import classes.ActionUtilPair;
import classes.ActionUtilPair.Action;
import classes.GridWorld;
import classes.State;
import util.ActionUtilHelper;
import util.Const;
import util.FuncHelper;

public class PolicyIterationApp {

	public static GridWorld _gridWorld = null;
	
	public static void main(String[] args) {
		
		_gridWorld = new GridWorld();
		
		// Displays the Grid World, just for debugging purposes to ensure correctness
		_gridWorld.displayGrid();
		
		State[][] _grid = _gridWorld.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + Const.DISCOUNT);
		System.out.println("k: " + Const.K + " (i.e. # of times simplified Bellman"
				+ " update is repeated to produce the next utility estimate)");
		
		// Perform value iteration
		List<ActionUtilPair[][]> lstActionUtilPairs = policyIteration(_grid);
		
		// Final item in the list is the optimal policy derived by value iteration
		final ActionUtilPair[][] optimalPolicy =
				lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);
		
		// Display the utilities of all the (non-wall) states
		ActionUtilHelper.displayUtilities(_grid, optimalPolicy);
		
		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		ActionUtilHelper.displayPolicy(optimalPolicy);
		
		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		ActionUtilHelper.displayUtilitiesGrid(optimalPolicy);
	}
	
	public static List<ActionUtilPair[][]> policyIteration(final State[][] grid) {
		
		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				
				currUtilArr[col][row] = new ActionUtilPair();
				if (!grid[col][row].isWall()) {
					currUtilArr[col][row].setAction(Action.getRandomAction());
				}
			}
		}
		
		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();
		boolean bUnchanged = true;
		int numIterations = 0;
		
		do {
			
			// Append to list of ActionUtilPair a copy of the existing actions & utilities
			ActionUtilPair[][] currUtilArrCopy =
					new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
			FuncHelper.array2DCopy(currUtilArr, currUtilArrCopy);
			lstActionUtilPairs.add(currUtilArrCopy);
			
			// Policy estimation
			ActionUtilPair[][] policyActionUtil = produceUtilEst(currUtilArr, grid);
			
			bUnchanged = true;
			
			// For each state - Policy improvement
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {

					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
						continue;

					ActionUtilPair bestActionUtil =
							FuncHelper.calcNewUtility(col, row, currUtilArr, grid);
					
					if(bestActionUtil.getUtil() > policyActionUtil[col][row].getUtil()) {
						
						policyActionUtil[col][row].setUtil(bestActionUtil.getUtil());
						policyActionUtil[col][row].setAction(bestActionUtil.getAction());
						bUnchanged = false;
					}
				}
			}
			
			FuncHelper.array2DCopy(policyActionUtil, currUtilArr);
			
			numIterations++;
			
		} while (!bUnchanged);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return lstActionUtilPairs;
	}
	
	/**
	 * Simplified Bellman update to produce the next utility estimate
	 * 
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The next utility estimates of all the states
	 */
	public static ActionUtilPair[][] produceUtilEst(ActionUtilPair[][] currUtilArr,
			final State[][] grid) {
		
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}
		FuncHelper.array2DCopy(currUtilArr, newUtilArr);
		
		int k = 0;
		do {
			
			// For each state
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {
	
					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
						continue;
	
					Action action = currUtilArr[col][row].getAction();
					switch (action) {
					case UP:
						newUtilArr[col][row].setUtil(FuncHelper
								.calcActionUpUtility(col, row, currUtilArr, grid));
						break;
					case DOWN:
						newUtilArr[col][row].setUtil(FuncHelper
								.calcActionDownUtility(col, row, currUtilArr, grid));
						break;
					case LEFT:
						newUtilArr[col][row].setUtil(FuncHelper
								.calcActionLeftUtility(col, row, currUtilArr, grid));
						break;
					case RIGHT:
						newUtilArr[col][row].setUtil(FuncHelper
								.calcActionRightUtility(col, row, currUtilArr, grid));
						break;
					}
				}
			}
			
			FuncHelper.array2DCopy(newUtilArr, currUtilArr);
			
		} while(++k <= Const.K);
		
		return newUtilArr;
	}
}
