package model;

import java.util.Random;

/**
 * Stores an action and utility pair for a given state
 */
public class ActionUtilPair implements Comparable<ActionUtilPair> {

	private Action _action = null;
	private double _util = 0.000;

	public ActionUtilPair() {
		_action = null;
		_util = 0.000;
	}

	public ActionUtilPair(Action action, double util) {
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
	public int compareTo(ActionUtilPair other) {

		// Descending order based on utility values
		return ((Double) other.getUtil()).compareTo(getUtil());
	}

	public enum Action {
		UP("^"),
		DOWN("v"),
		LEFT("<"),
		RIGHT(">");

		// String representation
		private String _strRep;

		Action(String strRep) {
			_strRep = strRep;
		}

		@Override
		public String toString() {
			return _strRep;
		}

		private static final Action[] ACTIONS = values();
		private static final int SIZE = ACTIONS.length;
		private static final Random RANDOM = new Random();

		public static Action getRandomAction() {
			return ACTIONS[RANDOM.nextInt(SIZE)];
		}
	}
}
