package model;

import java.util.Random;

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
