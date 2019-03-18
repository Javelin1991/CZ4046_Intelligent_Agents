package main;

import java.util.ArrayList;
import java.util.List;

import manager.Const;
import manager.DisplayManager;
import manager.FileManager;
import manager.UtilityManager;
import model.ActionUtilPair;
import model.GridEnvironment;
import model.State;

public class ValueIterationApp {

	public static GridEnvironment _GridEnvironment = null;

	public static void main(String[] args) {

		_GridEnvironment = new GridEnvironment();

		// Displays the Grid World, just for debugging purposes to ensure correctness
		_GridEnvironment.displayGrid();

		State[][] _grid = _GridEnvironment.getGrid();

		// Displays the settings currently used
		System.out.println("Discount: " + Const.DISCOUNT);
		System.out.println("Rmax: " + Const.R_MAX);
		System.out.println("Constant 'c': " + Const.C);
		System.out.println("Epsilon (c * Rmax): " + Const.EPSILON);

		// Perform value iteration
		List<ActionUtilPair[][]> lstActionUtilPairs = valueIteration(_grid);

		// Output to csv file to plot utility estimates as a function of iteration
		FileManager.writeToFile(lstActionUtilPairs, "value_iteration_utilities");

		// Final item in the list is the optimal policy derived by value iteration
		final ActionUtilPair[][] optimalPolicy =
				lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);

		// Display the utilities of all the (non-wall) states
		DisplayManager.displayUtilities(_grid, optimalPolicy);

		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		DisplayManager.displayPolicy(optimalPolicy);

		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		DisplayManager.displayUtilitiesGrid(optimalPolicy);
	}

	public static List<ActionUtilPair[][]> valueIteration(final State[][] grid) {

		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}

		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();

		// For using span semi-norm instead of max norm
		double deltaMax = Double.MIN_VALUE;
		double deltaMin = Double.MAX_VALUE;

		double convergenceCriteria =
				Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);
		System.out.printf("Convergence criteria: %.5f "
				+ "(i.e. the span semi-norm must be < this value)%n", convergenceCriteria);
		int numIterations = 0;

		do {

			UtilityManager.array2DCopy(newUtilArr, currUtilArr);
			deltaMax = Double.MIN_VALUE;
			deltaMin = Double.MAX_VALUE;

			// Append to list of ActionUtilPair a copy of the existing actions & utilities
			ActionUtilPair[][] currUtilArrCopy =
					new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
			UtilityManager.array2DCopy(currUtilArr, currUtilArrCopy);
			lstActionUtilPairs.add(currUtilArrCopy);

			// For each state
			for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
		        for(int col = 0 ; col < Const.NUM_COLS ; col++) {

		        	// Not necessary to calculate for walls
		        	if(grid[col][row].isWall())
		        		continue;

		        	newUtilArr[col][row] =
		        			UtilityManager.calcBestUtility(col, row, currUtilArr, grid);

		        	double newUtil = newUtilArr[col][row].getUtil();
		        	double currUtil = currUtilArr[col][row].getUtil();
		        	double sDelta = Math.abs(newUtil - currUtil);

		        	// Update maximum delta & minimum delta, if necessary
		        	deltaMax = Math.max(deltaMax, sDelta);
		        }
			}
			++numIterations;
			// Terminating condition: Span semi-norm less than the convergence criteria
		} while ((deltaMax) >= convergenceCriteria);

		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return lstActionUtilPairs;
	}
}
