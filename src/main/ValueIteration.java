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

public class ValueIteration {

	public static GridEnvironment _GridEnvironment = null;

	public static void main(String[] args) {

		_GridEnvironment = new GridEnvironment();

		// Displays the Grid Environment
		_GridEnvironment.displayGrid();

		State[][] _grid = _GridEnvironment.getGrid();

		// Displays the experiment setup
		DisplayManager.displayExperimentSetup();

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
		DisplayManager.displayPolicy(optimalPolicy);

		// Display the utilities of all states
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
		double delta = Double.MIN_VALUE;

		double convergenceCriteria =
				Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);
		System.out.printf("Convergence criteria: %.5f "
				+ "(i.e. the span semi-norm must be < this value)%n%n", convergenceCriteria);
		int numIterations = 0;

		do {

			UtilityManager.array2DCopy(newUtilArr, currUtilArr);

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
		        	double newDelta = Math.abs(newUtil - currUtil);

		        	// Update maximum delta & minimum delta, if necessary
		        	delta = Math.max(delta, newDelta);
		        }
			}
			++numIterations;
			// Terminating condition: Span semi-norm less than the convergence criteria
		} while ((delta) >= convergenceCriteria);
		DisplayManager.displayIterationsCount(numIterations);
		return lstActionUtilPairs;
	}
}
