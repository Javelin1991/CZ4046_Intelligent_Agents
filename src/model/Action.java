package model;

public enum Action {
	UP("^"),
	DOWN("v"),
	LEFT("<"),
	RIGHT(">");

	// String representation
	private String strRep;

	Action(String strRep) {
		this.strRep = strRep;
	}

	@Override
	public String toString() {
		return strRep;
	}
}
