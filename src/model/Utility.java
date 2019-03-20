package model;

import java.util.Random;

// Stores an action and utility pair for a given state
public class Utility implements Comparable<Utility> {

	private Action _action = null;
	private double _util = 0.000;

	public Utility() {
		_action = null;
		_util = 0.000;
	}

	public Utility(Action action, double util) {
		_action = action;
		_util = util;
	}

	public Action getAction() {
		return _action;
	}

	public String getActionStr() {

		// No action at wall, otherwise return one of the 4 possible actions
		return _action != null ? _action.toString() : " Wall";
	}

	public void setAction(Action action) {
		_action = action;
	}

	public double getUtil() {
		return _util;
	}

	public void setUtil(double util) {
		_util = util;
	}

	@Override
	public int compareTo(Utility other) {

		// Descending order based on utility values
		return ((Double) other.getUtil()).compareTo(getUtil());
	}

}
