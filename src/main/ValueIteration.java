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

	private static List<ActionUtilPair[][]> lstActionUtilPairs;
	private static State[][] _grid;
	private static int iterations = 0;
	private static double convergenceThreshold;

	public static void main(String[] args) {

		_GridEnvironment = new GridEnvironment();

		// Displays the Grid Environment
		_GridEnvironment.displayGrid();

		_grid = _GridEnvironment.getGrid();

		// Execute value iteration
		lstActionUtilPairs = runValueIteration(_grid);

		// Save utility estimates to csv file for plotting
		FileManager.writeToFile(lstActionUtilPairs, "value_iteration_utilities");

		// Display experiment results
		displayResults();
	}

	public static List<ActionUtilPair[][]> runValueIteration(final State[][] grid) {

		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}

		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();

		// Initialize delta to minimum double value first
		double delta = Double.MIN_VALUE;

		convergenceThreshold = Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);

		// Initialize number of iterations
		do {

			UtilityManager.array2DCopy(newUtilArr, currUtilArr);
			delta = Double.MIN_VALUE;

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

					double updatedUtil = newUtilArr[col][row].getUtil();
					double currentUtil = currUtilArr[col][row].getUtil();
					double updatedDelta = Math.abs(updatedUtil - currentUtil);

					// Update delta, if the updated delta value is larger than the current one
					delta = Math.max(delta, updatedDelta);
				}
			}
			iterations++;
		} while ((delta) >= convergenceThreshold); //the iteration will cease when the delta is less than the convergence threshold

		return lstActionUtilPairs;
	}

	private static void displayResults() {
		// Final item in the list is the optimal policy derived by value iteration
		final ActionUtilPair[][] optimalPolicy =
		lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);

		// Displays the experiment setup
		boolean isValueIteration = true;
		DisplayManager.displayExperimentSetup(isValueIteration);
		System.out.printf("Convergence Threshold\t:\t%.5f%n%n", convergenceThreshold);

		// Display total number of iterations required for convergence
		DisplayManager.displayIterationsCount(iterations);

		// Display the utilities of all the (non-wall) states
		DisplayManager.displayUtilities(_grid, optimalPolicy);

		// Display the optimal policy
		DisplayManager.displayPolicy(optimalPolicy);

		// Display the utilities of all states
		DisplayManager.displayUtilitiesGrid(optimalPolicy);
	}
}
