package main;

import java.util.ArrayList;
import java.util.List;

import model.Utility;
import model.Action;
import model.GridEnvironment;
import model.State;

import manager.UtilityManager;
import manager.Const;
import manager.FileManager;
import manager.DisplayManager;

public class PolicyIteration {

	public static GridEnvironment gridEnvironment = null;
	private static List<Utility[][]> finalUtilities;
	private static State[][] grid;
	private static int iterations = 0;
	private static boolean isValueIteration = false;

	public static void main(String[] args) {

		// Initialize grid environment
		gridEnvironment = new GridEnvironment();
		grid = gridEnvironment.getGrid();

		// Execute policy iteration
		runPolicyIteration(grid);

		// Display experiment results
		displayResults();

		// Save utility estimates to csv file for plotting
		FileManager.writeToFile(finalUtilities, "policy_iteration_utilities");
	}


	public static void runPolicyIteration(final State[][] grid) {

		Utility[][] currUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				currUtilArr[col][row] = new Utility();
				if (!grid[col][row].isWall()) {
					currUtilArr[col][row].setAction(Action.UP);
				}
			}
		}

		finalUtilities = new ArrayList<>();
		boolean unchanged = true;

		do {

			// Append to list of Utility a copy of the existing actions & utilities
			Utility[][] currUtilArrCopy =
			new Utility[Const.NUM_COLS][Const.NUM_ROWS];
			UtilityManager.updateUtilites(currUtilArr, currUtilArrCopy);
			finalUtilities.add(currUtilArrCopy);

			// Policy estimation
			Utility[][] policyActionUtil = UtilityManager.estimateNextUtilities(currUtilArr, grid);

			unchanged = true;

			// For each state - Policy improvement
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {

					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
					continue;

					// Best calculated action based on maximizing utility
					Utility bestActionUtil =
					UtilityManager.getBestUtility(col, row, policyActionUtil, grid);

					// Action and the corresponding utlity based on current policy
					Action policyAction = policyActionUtil[col][row].getAction();
					Utility pActionUtil = UtilityManager.getFixedUtility(
					policyAction, col, row, policyActionUtil, grid);

					if((bestActionUtil.getUtil() > pActionUtil.getUtil())) {

						policyActionUtil[col][row].setAction(bestActionUtil.getAction());
						unchanged = false;
					}
				}
			}

			UtilityManager.updateUtilites(policyActionUtil, currUtilArr);

			iterations++;

		} while (!unchanged);
	}


	private static void displayResults() {
		// Final item in the list is the optimal policy derived by policy iteration
		final Utility[][] optimalPolicy =
		finalUtilities.get(finalUtilities.size() - 1);

		// Displays the Grid Environment
		DisplayManager.displayGrid(grid);

		// Displays the experiment setup
		DisplayManager.displayExperimentSetup(isValueIteration, 0);

		// Display total number of iterations required for convergence
		DisplayManager.displayIterationsCount(iterations);

		// Display the utilities of all the (non-wall) states
		DisplayManager.displayUtilities(grid, optimalPolicy);

		// Display the optimal policy
		DisplayManager.displayPolicy(optimalPolicy);

		// Display the utilities of all states
		DisplayManager.displayUtilitiesGrid(optimalPolicy);
	}
}
