package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Utility;
import model.State;
import model.Action;

public class UtilityManager {

	//Calculates the utility for each possible action and returns the action with maximal utility
	public static Utility getBestUtility(final int col, final int row, final Utility[][] currUtilArr, final State[][] grid) {

		List<Utility> utilities = new ArrayList<>();

		utilities.add(new Utility(Action.UP, getActionUpUtility(col, row, currUtilArr, grid)));
		utilities.add(new Utility(Action.DOWN, getActionDownUtility(col, row, currUtilArr, grid)));
		utilities.add(new Utility(Action.LEFT, getActionLeftUtility(col, row, currUtilArr, grid)));
		utilities.add(new Utility(Action.RIGHT, getActionRightUtility(col, row, currUtilArr, grid)));

		Collections.sort(utilities);
		Utility bestUtility = utilities.get(0);
		return bestUtility;
	}


	//Calculates the utility for the given action
	public static Utility getFixedUtility(final Action action, final int col,
		final int row, final Utility[][] actionUtilArr, final State[][] grid) {

		Utility fixedActionUtil = null;

		switch (action) {
			case UP:
			fixedActionUtil = new Utility(Action.UP, UtilityManager.getActionUpUtility(col, row, actionUtilArr, grid));
			break;
			case DOWN:
			fixedActionUtil = new Utility(Action.DOWN, UtilityManager.getActionDownUtility(col, row, actionUtilArr, grid));
			break;
			case LEFT:
			fixedActionUtil = new Utility(Action.LEFT, UtilityManager.getActionLeftUtility(col, row, actionUtilArr, grid));
			break;
			case RIGHT:
			fixedActionUtil = new Utility(Action.RIGHT, UtilityManager.getActionRightUtility(col, row, actionUtilArr, grid));
			break;
		}

		return fixedActionUtil;
	}

	// Simplified Bellman update to produce the next utility estimate
	public static Utility[][] estimateNextUtilities(final Utility[][] utilArr, final State[][] grid) {

		Utility[][] currUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				currUtilArr[col][row] = new Utility();
			}
		}

		Utility[][] newUtilArr = new Utility[Const.NUM_COLS][Const.NUM_ROWS];
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				newUtilArr[col][row] = new Utility(utilArr[col][row].getAction(), utilArr[col][row].getUtil());
			}
		}


		int k = 0;
		do {
			UtilityManager.updateUtilites(newUtilArr, currUtilArr);

			// For each state
			for (int row = 0; row < Const.NUM_ROWS; row++) {
				for (int col = 0; col < Const.NUM_COLS; col++) {
					if (!grid[col][row].isWall()) {
						// Updates the utility based on the action stated in the policy
						Action action = currUtilArr[col][row].getAction();
						newUtilArr[col][row] = UtilityManager.getFixedUtility(action,
						col, row, currUtilArr, grid);
					}
				}
			}
			k++;
		} while(k < Const.K);

		return newUtilArr;
	}


	//Calculates the utility for attempting to move up
	public static double getActionUpUtility(final int col, final int row,
		final Utility[][] currUtilArr, final State[][] grid) {

		double actionUpUtility = 0.000;

		// Intends to move up
		actionUpUtility += Const.PROB_INTENT * moveUp(col, row, currUtilArr, grid);

		// Intends to move up, but moves right instead
		actionUpUtility += Const.PROB_RIGHT * moveRight(col, row, currUtilArr, grid);

		// Intends to move up, but moves left instead
		actionUpUtility += Const.PROB_LEFT * moveLeft(col, row, currUtilArr, grid);

		// Final utility
		actionUpUtility = grid[col][row].getReward() + Const.DISCOUNT * actionUpUtility;

		return actionUpUtility;
	}

	//Calculates the utility for attempting to move down
	public static double getActionDownUtility(final int col, final int row,
	final Utility[][] currUtilArr, final State[][] grid) {

		double actionDownUtility = 0.000;

		// Intends to move down
		actionDownUtility += Const.PROB_INTENT * moveDown(col, row, currUtilArr, grid);

		// Intends to move down, but moves left instead
		actionDownUtility += Const.PROB_LEFT * moveLeft(col, row, currUtilArr, grid);

		// Intends to move down, but moves right instead
		actionDownUtility += Const.PROB_RIGHT * moveRight(col, row, currUtilArr, grid);

		// Final utility
		actionDownUtility = grid[col][row].getReward() + Const.DISCOUNT * actionDownUtility;

		return actionDownUtility;
	}

	//Calculates the utility for attempting to move left
	public static double getActionLeftUtility(final int col, final int row,
		final Utility[][] currUtilArr, final State[][] grid) {

		double actionLeftUtility = 0.000;

		// Intends to move left
		actionLeftUtility += Const.PROB_INTENT * moveLeft(col, row, currUtilArr, grid);

		// Intends to move left, but moves up instead
		actionLeftUtility += Const.PROB_RIGHT * moveUp(col, row, currUtilArr, grid);

		// Intends to move left, but moves down instead
		actionLeftUtility += Const.PROB_LEFT * moveDown(col, row, currUtilArr, grid);

		// Final utility
		actionLeftUtility = grid[col][row].getReward() + Const.DISCOUNT * actionLeftUtility;

		return actionLeftUtility;
	}

	// Calculates the utility for attempting to move right
	public static double getActionRightUtility(final int col, final int row,
		final Utility[][] currUtilArr, final State[][] grid) {

		double actionRightUtility = 0.000;

		// Intends to move right
		actionRightUtility += Const.PROB_INTENT * moveRight(col, row, currUtilArr, grid);

		// Intends to move right, but moves down instead
		actionRightUtility += Const.PROB_RIGHT * moveDown(col, row, currUtilArr, grid);

		// Intends to move right, but moves up instead
		actionRightUtility += Const.PROB_LEFT * moveUp(col, row, currUtilArr, grid);

		// Final utility
		actionRightUtility = grid[col][row].getReward() + Const.DISCOUNT * actionRightUtility;

		return actionRightUtility;
	}

	// Attempts to move up
	public static double moveUp(final int col, final int row, final Utility[][] currUtilArr, final State[][] grid) {

		if (row - 1 >= 0 && !grid[col][row - 1].isWall()) {
			return currUtilArr[col][row - 1].getUtil();
		}
		return currUtilArr[col][row].getUtil();
	}

	// Attempts to move down
	public static double moveDown(final int col, final int row, final Utility[][] currUtilArr, final State[][] grid) {
		if (row + 1 < Const.NUM_ROWS && !grid[col][row + 1].isWall()) {
			return currUtilArr[col][row + 1].getUtil();
		}
		return currUtilArr[col][row].getUtil();
	}

	// Attempts to move left
	public static double moveLeft(final int col, final int row, final Utility[][] currUtilArr, final State[][] grid) {
		if (col - 1 >= 0 && !grid[col - 1][row].isWall()) {
			return currUtilArr[col - 1][row].getUtil();
		}
		return currUtilArr[col][row].getUtil();
	}

	// Attempts to move right
	public static double moveRight(final int col, final int row, final Utility[][] currUtilArr, final State[][] grid) {
		if (col + 1 < Const.NUM_COLS && !grid[col + 1][row].isWall()) {
			return currUtilArr[col + 1][row].getUtil();
		}
		return currUtilArr[col][row].getUtil();
	}

	// Copy the contents from the source array to the destination array
	public static void updateUtilites(Utility[][] aSrc, Utility[][] aDest) {
		for (int i = 0; i < aSrc.length; i++) {
			System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
		}
	}
}
