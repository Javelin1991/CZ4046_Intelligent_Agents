package util;

public class Const {

	// Size of the Grid World
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	
	// Reward functions
	public static final double WHITE_REWARD = -0.040;
	public static final double GREEN_REWARD = +1.000;
	public static final double BROWN_REWARD = -1.000;
	public static final double WALL_REWARD = 0.000;
	
	/// Transition model
	public static final double PROB_INTENT = 0.800;
	public static final double PROB_CCW = 0.100;
	public static final double PROB_CW = 0.100;
	
	// Delimiters
	public static final String GRID_DELIM = ";";
	public static final String COL_ROW_DELIM = ",";
	
	// Grid World information in (col, row) format delimited by semi-colon
	public static final String GREEN_SQUARES = "0,0; 2,0; 5,0; 3,1; 4,2; 5,3";
	public static final String BROWN_SQUARES = "1,1; 5,1; 2,2; 3,3; 4,4";
	public static final String WALLS_SQUARES = "1,0; 4,1; 1,4; 2,4; 3,4";
	
	// Agent's starting position
	// NOTE: A remarkable consequence of using discounted utilities with infinite
	// horizons is that the optimal policy is independent of the starting state
	public static final int AGENT_START_COL = 2;
	public static final int AGENT_START_ROW = 3;
	
	// Discount factor
	public static final double DISCOUNT =  0.990;	// FIXME: Should be 0.99 based on question
	// FIXME : 0.946350000
	
	// Rmax
	public static final double R_MAX = 1.000;
	
	// Constant c
	public static final double C = 0.100;	// FIXME: What to use for constant c
	
	// Epsilon e = c * Rmax
	public static final double EPSILON = C * R_MAX;
}
