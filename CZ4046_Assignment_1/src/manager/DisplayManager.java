package manager;

import java.text.DecimalFormat;

import model.Utility;
import model.State;

public class DisplayManager {

	// Display the Grid Environment
	public static void displayGrid(State[][] grid) {
		StringBuilder sb = DisplayManager.frameTitle("Grid Environment");
		sb.append("|");
		for(int col = 0 ; col < Const.NUM_COLS ; col++) {
			sb.append("--------|");
		}
		sb.append("\n");

		for (int row = 0; row < Const.NUM_ROWS; row++) {

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {

				State state = grid[col][row];
				String temp;
				if (col == Const.AGENT_START_COL && row == Const.AGENT_START_ROW) {
					temp = " Start";
				} else if(state.isWall()) {
					temp = "Wall";
				}
				else if(state.getReward() != Const.WHITE_REWARD) {
					temp = Double.toString(state.getReward());
					if (temp.charAt(0) != '-') {
						temp = " " + temp;
					}
				}
				else {
					temp = String.format("%4s", "");
				}
				int n = (8 - temp.length())/2;
				String str = String.format("%1$"+n+"s", "");
				sb.append(str + temp + str + "|");
			}

			sb.append("\n|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|");
			}
			sb.append("\n");
		}

		System.out.println(sb.toString());
	}

	// Display the policy, i.e. the action to be taken at each state
	public static void displayPolicy(final Utility[][] utilArr) {
		StringBuilder sb = frameTitle("Plot of Optimal Policy");
		sb.append("|");
		for(int col = 0 ; col < Const.NUM_COLS ; col++) {
			sb.append("--------|");
		}
		sb.append("\n");
		for (int row = 0; row < Const.NUM_ROWS; row++) {

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for (int col = 0; col < Const.NUM_COLS; col++) {
				String util = utilArr[col][row].getActionStr();
				int n = (9 - util.length())/2;
				String str = String.format("%1$"+n+"s", "");
				String str1 = String.format("%1$"+(n-1)+"s", "");
				sb.append(str + util + str1 + "|");
			}

			sb.append("\n|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|");
			}
			sb.append("\n");
		}

		System.out.println(sb.toString());
	}

	// Display the utilities of all the (non-wall) states
	public static void displayUtilities(final State[][] grid, final Utility[][] utilArr) {
		StringBuilder sb = frameTitle("Utility Values of States");
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				if (!grid[col][row].isWall()) {
					String util = String.format("%.8g", utilArr[col][row].getUtil());
					sb.append("(" + col + ", " + row + "): " + util + "\n");
				}
			}
		}
		System.out.println(sb.toString());
	}

	// Display the utilities of all the states, in a grid format
	public static void displayUtilitiesGrid(final Utility[][] utilArr) {

		StringBuilder sb = frameTitle("Utilities of All States (Map)");

		sb.append("|");
		for(int col = 0 ; col < Const.NUM_COLS ; col++) {
			sb.append("--------|");
		}
		sb.append("\n");

		String pattern = "00.000";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		for (int row = 0; row < Const.NUM_ROWS; row++) {

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for (int col = 0; col < Const.NUM_COLS; col++) {

				sb.append(String.format(" %s |",
				decimalFormat.format(utilArr[col][row].getUtil()).substring(0, 6)));
			}

			sb.append("\n|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for(int col = 0 ; col < Const.NUM_COLS ; col++) {
				sb.append("--------|");
			}
			sb.append("\n");
		}

		System.out.println(sb.toString());
	}

	// Display the number of iterations
	public static void displayIterationsCount(int num) {
		StringBuilder sb = frameTitle("Total Iteration Count");
		sb.append("Iterations: " + num + "\n");
		System.out.println(sb.toString());
	}

	// Display the experiment setup for value iteration
	public static void displayExperimentSetup(boolean isValueIteration, double convergeThreshold) {
		StringBuilder sb = frameTitle("Experiment Setup");
		if (isValueIteration) {
			sb.append("Discount Factor\t\t" + ":\t" + Const.DISCOUNT + "\n");
			sb.append("Utility Upper Bound\t" + ":\t" + String.format("%.5g", Const.UTILITY_UPPER_BOUND) + "\n");
			sb.append("Max Reward(Rmax)\t" + ":\t" + Const.R_MAX + "\n");
			sb.append("Constant 'c'\t\t" + ":\t" + Const.C + "\n");
			sb.append("Epsilon Value(c * Rmax)\t" + ":\t" + Const.EPSILON + "\n");
			sb.append("Convergence Threshold\t:\t" + String.format("%.5f", convergeThreshold) + "\n\n");
		} else {
			sb.append("Discount\t:\t" + Const.DISCOUNT + "\n");
			//(i.e. # of times simplified Bellman update is repeated to produce the next utility estimate)
			sb.append("k\t\t:\t" + Const.K + "\n\n");
		}
		System.out.print(sb.toString());
	}

	public static StringBuilder frameTitle(String str) {
		StringBuilder sb = new StringBuilder();
		int padding = 4;
		sb.append("\n");
		for(int i = 0; i < str.length()+padding; i++) {
			sb.append("*");
		}
		sb.append("\n");
		sb.append("* " + str + " *");
		sb.append("\n");
		for(int i = 0; i < str.length()+padding; i++) {
			sb.append("*");
		}
		sb.append("\n");
		sb.append("\n");
		return sb;
	}

}
