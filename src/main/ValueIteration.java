package main;

import java.util.ArrayList;
import java.util.List;

import manager.Const;
import manager.DisplayManager;
import manager.FileManager;
import manager.UtilityManager;

import model.Utility;
import model.GridEnvironment;
import model.State;

public class ValueIteration {

	public static GridEnvironment gridEnvironment;
	private static List<Utility[][]> utilityList;
	private static State[][] grid;
	private static int iterations = 0;
	private static double convergeThreshold;
	private static boolean isValueIteration = true;

	public static void main(String[] args) {

		// Initialize grid environment
		gridEnvironment = new GridEnvironment();
		grid = gridEnvironment.getGrid();

		// Execute value iteration
		runValueIteration(grid);

		// Display experiment results
		displayResults();

		// Save utility estimates to csv file for plotting
		FileManager.writeToFile(utilityList, "value_iteration_utilities");
	}

	public static void runValueIteration(final State[][] grid) {

		Utility[][] currUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];
		Utility[][] newUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];

		// Initialize default utilities for each state
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new Utility();
			}
		}

		utilityList = new ArrayList<>();

		// Initialize delta to minimum double value first
		double delta = Double.MIN_VALUE;

		convergeThreshold = Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);

		// Initialize number of iterations
		do {

			UtilityManager.updateUtilites(newUtilArr, currUtilArr);
			delta = Double.MIN_VALUE;

			// Append utilities of each state achieved so far (until current iteration) to a list of utility
			 utilityList.add(currUtilArr);

			// For each state
			for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
				for(int col = 0 ; col < Const.NUM_COLS ; col++) {

					// Calculate the utility for each state, not necessary to calculate for walls
					if (!grid[col][row].isWall()) {
						newUtilArr[col][row] =
						UtilityManager.getBestUtility(col, row, currUtilArr, grid);

						double updatedUtil = newUtilArr[col][row].getUtil();
						double currentUtil = currUtilArr[col][row].getUtil();
						double updatedDelta = Math.abs(updatedUtil - currentUtil);

						// Update delta, if the updated delta value is larger than the current one
						delta = Math.max(delta, updatedDelta);
					}
				}
			}
			iterations++;

		//the iteration will cease when the delta is less than the convergence threshold
		} while ((delta) >= convergeThreshold);
	}

	private static void displayResults() {
		// Final item in the list is the optimal policy derived by value iteration
		int lastIteration = utilityList.size() - 1;
		final Utility[][] optimalPolicy =
		utilityList.get(lastIteration);

		// Displays the Grid Environment
		DisplayManager.displayGrid(grid);

		// Displays the experiment setup
		DisplayManager.displayExperimentSetup(isValueIteration, convergeThreshold);

		// Display total number of iterations required for convergence
		DisplayManager.displayIterationsCount(iterations);

		// Display the final utilities of all the (non-wall) states
		DisplayManager.displayUtilities(grid, optimalPolicy);

		// Display the optimal policy
		DisplayManager.displayPolicy(optimalPolicy);

		// Display the final utilities of all states
		DisplayManager.displayUtilitiesGrid(optimalPolicy);
	}
}
