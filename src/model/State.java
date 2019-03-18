package model;

/** A single state **/
public class State {

	private double _reward = 0.000;

	private boolean _bIsWall = false;

	public State(double reward) {
		_reward = reward;
	}

	public double getReward() {
		return _reward;
	}

	public void setReward(double reward) {
		_reward = reward;
	}

	public boolean isWall() {
		return _bIsWall;
	}

	public void setAsWall(boolean bIsWall) {
		_bIsWall = bIsWall;
	}
}
