package manager;

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
	public static final double PROB_LEFT = 0.100;
	public static final double PROB_RIGHT = 0.100;

	// Delimiters
	public static final String GRID_DELIM = ";";
	public static final String COL_ROW_DELIM = ",";

	// Grid Environment information in (col, row) format delimited by semi-colon
	public static final String GREEN_SQUARES = "0,0; 2,0; 5,0; 3,1; 4,2; 5,3";
	public static final String BROWN_SQUARES = "1,1; 5,1; 2,2; 3,3; 4,4";
	public static final String WALLS_SQUARES = "1,0; 4,1; 1,4; 2,4; 3,4";

	// Agent's starting position
	// NOTE: A remarkable consequence of using discounted utilities with infinite
	// horizons is that the optimal policy is independent of the starting state

	public static final int AGENT_START_COL = 2; // first col starts from 0
	public static final int AGENT_START_ROW = 3; // first row starts from 0

	// Discount factor
	public static final double DISCOUNT =  0.990;

	// Rmax
	public static final double R_MAX = 1.000;

	// Constant c
	public static final double C = 50;	//constant parameter to adjust the maximum error allowed
	// Epsilon e = c * Rmax
	public static final double EPSILON = C * R_MAX;

	public static final double UTILITY_UPPER_BOUND = R_MAX / (1 - DISCOUNT);

	// Constant k (i.e. number of times simplified Bellman update is executed
	// to produce the next utility estimate)
	public static final int K = 10;
}
