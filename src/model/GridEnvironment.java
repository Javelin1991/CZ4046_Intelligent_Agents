package model;

import manager.Const;

public class GridEnvironment {

	private State[][] _grid = null;

	public GridEnvironment() {

		_grid = new State[Const.NUM_COLS][Const.NUM_ROWS];
		buildGrid();
		duplicateGrid();
	}

	/** Returns the actual grid, i.e. a 2-D states array **/
	public State[][] getGrid() {
		return _grid;
	}

	/**
	 * Initialize the Grid World
	 */
	public void buildGrid() {

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
	 * Used to 'expand' the maze
	 */
	public void duplicateGrid() {

		for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {

				if (row >= 6 || col >= 6) {
					int trueRow = row % 6;
					int trueCol = col % 6;

					_grid[col][row].setReward(_grid[trueCol][trueRow].getReward());
					_grid[col][row].setAsWall(_grid[trueCol][trueRow].isWall());
				}
	        }
	    }
	}

	/**
	 * Display the Grid World
	 */
	public void displayGrid() {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 20; i++) {
			sb.append("*");
		}
		sb.append("\n");
		sb.append("* Grid Environment *");
		sb.append("\n");
		for(int i = 0; i < 20; i++) {
			sb.append("*");
		}

		sb.append("\n");
        sb.append("\n");

        sb.append("|");
        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
        	sb.append("------|");
        }
        sb.append("\n");

		for(int row = 0 ; row < Const.NUM_ROWS ; row++) {
	        sb.append("|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {

	        	State state = _grid[col][row];

	        	if (col == 2 && row == 3) {
	                sb.append(String.format(" %-2s|", "Start"));
	        	} else if(state.isWall()) {
	                sb.append(String.format(" %-2s |", "Wall"));
	        	}
	        	else if(state.getReward() != Const.WHITE_REWARD) {
	        		sb.append(String.format("  %+1.0f  |", state.getReward()));
	        	}
	        	else {
	        		sb.append(String.format("%4s  |", ""));
	        	}
	        }

	        sb.append("\n|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	sb.append("------|");
	        }
	        sb.append("\n");
	    }

		System.out.println(sb.toString());
	}
}
