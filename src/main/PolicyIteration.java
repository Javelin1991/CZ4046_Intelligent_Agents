package main;

import java.util.ArrayList;
import java.util.List;

import model.ActionUtilPair;
import model.ActionUtilPair.Action;
import model.GridEnvironment;
import model.State;

import manager.UtilityManager;
import manager.Const;
import manager.FileManager;
import manager.DisplayManager;

public class PolicyIteration {

	public static GridEnvironment _GridEnvironment = null;

	private static List<ActionUtilPair[][]> lstActionUtilPairs;
	private static State[][] _grid;
	private static int iterations = 0;

	public static void main(String[] args) {

		_GridEnvironment = new GridEnvironment();
		// Displays the Grid Environment
		_GridEnvironment.displayGrid();

		_grid = _GridEnvironment.getGrid();

		// Execute policy iteration
		lstActionUtilPairs = runPolicyIteration(_grid);

		// Save utility estimates to csv file for plotting
		FileManager.writeToFile(lstActionUtilPairs, "policy_iteration_utilities");

		// Display experiment results
		displayResults();
	}


	public static List<ActionUtilPair[][]> runPolicyIteration(final State[][] grid) {

		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				currUtilArr[col][row] = new ActionUtilPair();
				if (!grid[col][row].isWall()) {
					currUtilArr[col][row].setAction(Action.UP);
				}
			}
		}

		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();
		boolean bUnchanged = true;

		do {

			// Append to list of ActionUtilPair a copy of the existing actions & utilities
			ActionUtilPair[][] currUtilArrCopy =
			new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
			UtilityManager.array2DCopy(currUtilArr, currUtilArrCopy);
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

					// Best calculated action based on maximizing utility
					ActionUtilPair bestActionUtil =
					UtilityManager.calcBestUtility(col, row, policyActionUtil, grid);

					// Action and the corresponding utlity based on current policy
					Action policyAction = policyActionUtil[col][row].getAction();
					ActionUtilPair pActionUtil = UtilityManager.calcFixedUtility(
					policyAction, col, row, policyActionUtil, grid);

					if((bestActionUtil.getUtil() > pActionUtil.getUtil())) {

						policyActionUtil[col][row].setAction(bestActionUtil.getAction());
						bUnchanged = false;
					}
				}
			}

			UtilityManager.array2DCopy(policyActionUtil, currUtilArr);

			iterations++;

		} while (!bUnchanged);
		return lstActionUtilPairs;
	}

	//Simplified Bellman update to produce the next utility estimate
	public static ActionUtilPair[][] produceUtilEst(final ActionUtilPair[][] currUtilArr,
	final State[][] grid) {

		ActionUtilPair[][] currUtilArrCpy = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				currUtilArrCpy[col][row] = new ActionUtilPair(
				currUtilArr[col][row].getAction(),
				currUtilArr[col][row].getUtil());
			}
		}

		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}

		int k = 0;
		do {

			// For each state
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {

					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
					continue;

					// Updates the utility based on the action stated in the policy
					Action action = currUtilArrCpy[col][row].getAction();
					newUtilArr[col][row] = UtilityManager.calcFixedUtility(action,
					col, row, currUtilArrCpy, grid);
				}
			}

			UtilityManager.array2DCopy(newUtilArr, currUtilArrCpy);

		} while(++k < Const.K);

		return newUtilArr;
	}

	private static void displayResults() {
		// Final item in the list is the optimal policy derived by policy iteration
		final ActionUtilPair[][] optimalPolicy =
		lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);
		// Displays the experiment setup
		boolean isValueIteration = false;
		DisplayManager.displayExperimentSetup(isValueIteration);

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
