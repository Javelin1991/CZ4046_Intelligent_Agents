package main;

import classes.State;
import util.Const;

public class GridWorld {

	public static State[][] _grid = new State[Const.NUM_COLS][Const.NUM_ROWS];
	
	public static void main(String[] args) {
		
		buildGrid();
		
		// Displays the Grid World, just for debugging purposes to ensure correctness
		displayGrid();
		
		// Try value iteration
		double[][] utilities = valueIteration(_grid);
		
		// Display the utilities of all the (non-wall) states
		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				if (!_grid[col][row].isWall()) {
					System.out.println("(" + col + ", " + row + "): "
							+ String.format("%-2.6f", utilities[col][row]));
				}
			}
		}
	}
	
	/**
	 * Initialize the Grid World
	 */
	public static void buildGrid() {
		
		// All grids (even walls) starts with reward of -0.040
		for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	
	        	_grid[col][row] = new State(Const.WHITE_REWARD);
	        }
	    }
		
		// Set all the green squares (+1.000)
		String[] greenSquaresArr = Const.GREEN_SQUARES.split(Const.GRID_DELIM);
		for(String greenSquare : greenSquaresArr) {
			
			greenSquare = greenSquare.trim();
			String [] gridInfo = greenSquare.split(Const.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);
			
			_grid[gridCol][gridRow].setReward(Const.GREEN_REWARD);
		}
		
		// Set all the brown squares (-1.000)
		String[] brownSquaresArr = Const.BROWN_SQUARES.split(Const.GRID_DELIM);
		for (String brownSquare : brownSquaresArr) {

			brownSquare = brownSquare.trim();
			String[] gridInfo = brownSquare.split(Const.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			_grid[gridCol][gridRow].setReward(Const.BROWN_REWARD);
		}
		
		// Set all the walls (0.000 and unreachable, i.e. stays in the same place as before)
		String[] wallSquaresArr = Const.WALLS_SQUARES.split(Const.GRID_DELIM);
		for (String wallSquare : wallSquaresArr) {

			wallSquare = wallSquare.trim();
			String[] gridInfo = wallSquare.split(Const.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			_grid[gridCol][gridRow].setReward(Const.WALL_REWARD);
			_grid[gridCol][gridRow].setAsWall(true);
		}
	}
	
	/**
	 * Display the Grid World
	 */
	public static void displayGrid() {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 9; i++) {
			sb.append("=");
		}
		sb.append(" Grid  World ");
		for(int i = 0; i < 9; i++) {
			sb.append("=");
		}
		sb.append("\n");
		
        sb.append("|");
        for(int columnIndex = 0 ; columnIndex < Const.NUM_COLS ; columnIndex++) {
        	sb.append("----|");
        }
        sb.append("\n");
        
		for(int rowIndex = 0 ; rowIndex < Const.NUM_ROWS ; rowIndex++) {
	        sb.append("|");
	        for(int columnIndex = 0 ; columnIndex < Const.NUM_COLS ; columnIndex++) {
	        	
	        	State state = _grid[columnIndex][rowIndex];
	        	if(state.isWall()) {
	                sb.append(String.format(" %-2s |", "WW"));
	        	}
	        	else if(state.getReward() != Const.WHITE_REWARD) {
	        		sb.append(String.format(" %+1.0f |", state.getReward()));
	        	}
	        	else {
	        		sb.append(String.format("%4s|", ""));
	        	}
	        }
	        
	        sb.append("\n|");
	        for(int columnIndex = 0 ; columnIndex < Const.NUM_COLS ; columnIndex++) {
	        	sb.append("----|");
	        }
	        sb.append("\n");
	    }
		
		System.out.println(sb.toString());
	}
	
	public static double[][] valueIteration(final State[][] _grid) {
		
		double[][] currUtilArr = new double[Const.NUM_COLS][Const.NUM_ROWS];
		double[][] newUtilArr = new double[Const.NUM_COLS][Const.NUM_ROWS];
		double delta = 0.000;
		
		double convergenceCriteria = Const.EPSILON * ((1.000 - Const.DISCOUNT) / Const.DISCOUNT);
		
		int numIterations = 0;
		
		do {
			
			array2DCopy(newUtilArr, currUtilArr);
			delta = 0.000;
			
			// For each state
			for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
		        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
		        	
		        	// Not necessary to calculate for walls
		        	if(_grid[col][row].isWall())
		        		continue;
		        	
		        	newUtilArr[col][row] = calcNewUtility(col, row, currUtilArr, _grid);
		        	
		        	// Update maximum change (delta) if necessary
		        	double sDelta = Math.abs(newUtilArr[col][row] - currUtilArr[col][row]);
		        	if(sDelta > delta) {
		        		delta = sDelta;
		        	}
		        }
			}
			
			// Print newUtilityArr?
			numIterations++;
			
		} while (delta >= convergenceCriteria);
		
		System.out.println("Number of iterations: " + numIterations);
		return currUtilArr;
	}
	
	public static void array2DCopy(double[][] aSrc, double[][] aDest) {
	    for (int i = 0; i < aSrc.length; i++) {
	        System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
	    }
	}
	
	public static double calcNewUtility(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		double newUtility = _grid[col][row].getReward();
		
		double actionUpUtility = calcActionUpUtility(col, row, currUtilArr, _grid);
		double actionDownUtility = calcActionDownUtility(col, row, currUtilArr, _grid);
		double actionLeftUtility = calcActionLeftUtility(col, row, currUtilArr, _grid);
		double actionRightUtility = calcActionRightUtility(col, row, currUtilArr, _grid);
		
		newUtility += (Const.DISCOUNT * Math.max(
				Math.max(actionUpUtility, actionDownUtility),
				Math.max(actionLeftUtility, actionRightUtility)));
		
		return newUtility;
	}
	
	public static double calcActionUpUtility(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		double actionUpUtility = 0.000;
		
		// Intends to move up
		actionUpUtility += Const.PROB_INTENT * goUp(col, row, currUtilArr, _grid);
		
		// Intends to move up, but goes right (CW) instead
		actionUpUtility += Const.PROB_CW * goRight(col, row, currUtilArr, _grid);
		
		// Intends to move up, but goes left (CCW) instead
		actionUpUtility += Const.PROB_CCW * goLeft(col, row, currUtilArr, _grid);
		
		return actionUpUtility;
	}
	
	public static double calcActionDownUtility(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		double actionDownUtility = 0.000;
		
		// Intends to move down
		actionDownUtility += Const.PROB_INTENT * goDown(col, row, currUtilArr, _grid);
		
		// Intends to move down, but goes left (CW) instead
		actionDownUtility += Const.PROB_CW * goLeft(col, row, currUtilArr, _grid);
		
		// Intends to move down, but goes right (CCW) instead
		actionDownUtility += Const.PROB_CCW * goRight(col, row, currUtilArr, _grid);
		
		return actionDownUtility;
	}
	
	public static double calcActionLeftUtility(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		double actionLeftUtility = 0.000;
		
		// Intends to move left
		actionLeftUtility += Const.PROB_INTENT * goLeft(col, row, currUtilArr, _grid);
		
		// Intends to move left, but goes up (CW) instead
		actionLeftUtility += Const.PROB_CW * goUp(col, row, currUtilArr, _grid);
		
		// Intends to move left, but goes down (CCW) instead
		actionLeftUtility += Const.PROB_CCW * goDown(col, row, currUtilArr, _grid);
		
		return actionLeftUtility;
	}
	
	public static double calcActionRightUtility(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		double actionRightUtility = 0.000;
		
		// Intends to move right
		actionRightUtility += Const.PROB_INTENT * goRight(col, row, currUtilArr, _grid);
		
		// Intends to move right, but goes down (CW) instead
		actionRightUtility += Const.PROB_CW * goDown(col, row, currUtilArr, _grid);
		
		// Intends to move right, but goes up (CCW) instead
		actionRightUtility += Const.PROB_CCW * goUp(col, row, currUtilArr, _grid);
		
		return actionRightUtility;
	}
	
	/**
	 * Attempts to go up<br>
	 * Succeeds if the target state is not out-of-bounds and not a wall<br>
	 * Failure results in the agent staying in the same place as before<br>
	 * Returns the utility value of the resulting state
	 * 
	 * @param col			Current column
	 * @param row			Current row
	 * @param currUtilArr	Array of the current utility values
	 * @param _grid			The Grid World
	 * @return				Utility value of the resulting state
	 */
	public static double goUp(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		return (row - 1 >= 0 && !_grid[col][row - 1].isWall()) ?
				currUtilArr[col][row - 1] : currUtilArr[col][row];
	}
	
	public static double goDown(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		return (row + 1 < Const.NUM_ROWS && !_grid[col][row + 1].isWall()) ?
				currUtilArr[col][row + 1] : currUtilArr[col][row];
	}
	
	public static double goLeft(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		return (col - 1 >= 0 && !_grid[col - 1][row].isWall()) ?
				currUtilArr[col - 1][row] : currUtilArr[col][row];
	}
	
	public static double goRight(final int col, final int row,
			final double[][] currUtilArr, final State[][] _grid) {
		
		return (col + 1 < Const.NUM_COLS && !_grid[col + 1][row].isWall()) ?
				currUtilArr[col + 1][row] : currUtilArr[col][row];
	}
}
