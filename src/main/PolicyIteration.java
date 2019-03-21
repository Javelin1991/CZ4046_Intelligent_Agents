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
	private static List<Utility[][]> utilityList;
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
		FileManager.writeToFile(utilityList, "policy_iteration_utilities");
	}


	public static void runPolicyIteration(final State[][] grid) {

		Utility[][] currUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];
		Utility[][] newUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];

		// Initialize default utilities and policies for each state
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new Utility();
				if (!grid[col][row].isWall()) {
					newUtilArr[col][row].setAction(Action.UP);
				}
			}
		}

		// List to store utilities of every state at each iteration
		utilityList = new ArrayList<>();

		// Used to check if the current policy value is already optimal
		boolean unchanged = true;

		do {

			UtilityManager.updateUtilites(newUtilArr, currUtilArr);

			// Append to list of Utility a copy of the existing actions & utilities
			Utility[][] currUtilArrCopy =
			new Utility[Const.NUM_COLS][Const.NUM_ROWS];
			UtilityManager.updateUtilites(currUtilArr, currUtilArrCopy);
			utilityList.add(currUtilArrCopy);

			// Policy estimation based on the current utilites
			newUtilArr = UtilityManager.estimateNextUtilities(currUtilArr, grid);

			unchanged = true;

			// For each state - Policy improvement
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {

					// Calculate the utility for each state, not necessary to calculate for walls
					if (!grid[col][row].isWall()) {
						// Best calculated action based on maximizing utility
						Utility bestActionUtil =
						UtilityManager.getBestUtility(col, row, newUtilArr, grid);

						// Action and the corresponding utlity based on current policy
						Action policyAction = newUtilArr[col][row].getAction();
						Utility policyActionUtil = UtilityManager.getFixedUtility(policyAction, col, row, newUtilArr, grid);

						if((bestActionUtil.getUtil() > policyActionUtil.getUtil())) {
							newUtilArr[col][row].setAction(bestActionUtil.getAction());
							unchanged = false;
						}
					}
				}
			}
			iterations++;

		} while (!unchanged);
	}


	private static void displayResults() {
		// Final item in the list is the optimal policy derived by policy iteration
		int latestUtilities = utilityList.size() - 1;
		final Utility[][] optimalPolicy =
		utilityList.get(latestUtilities);

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
