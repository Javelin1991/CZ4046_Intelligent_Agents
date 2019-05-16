package model;

// A single state
public class State {

	private double reward = 0.000;

	private boolean hasWall = false;

	public State(double reward) {
		this.reward = reward;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public boolean isWall() {
		return hasWall;
	}

	public void setAsWall(boolean hasWall) {
		this.hasWall = hasWall;
	}
}
