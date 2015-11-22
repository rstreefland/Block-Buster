package uk.ac.rdg.rhys.blockbuster;

public class Ball {

	public int type;
	
	public float width;
	public float height;

	// The X and Y position of the ball on the screen (middle of ball)
	public float mBallX = 0;
	public float mBallY = 0;

	// The speed (pixel/second) of the ball in direction X and Y
	public float mBallSpeedX = 0;
	public float mBallSpeedY = 0;

	public boolean isActive;
	
	public long startTimer = 0;

	public Ball(GameView localGameView, boolean newIsActive, float cellWidth,
			float cellHeight) {
		type = 0;
		isActive = newIsActive;
	}

	public Ball(GameView localGameView, boolean newIsActive, float x, float y,
			float speedX, float speedY, float cellWidth,
			float cellHeight) {
		type = 0;
		isActive = newIsActive;
		
		mBallX = x;
		mBallY = y;
		mBallSpeedX = speedX;
		mBallSpeedY = speedY;
	}

	public void makeActive(float x, float y) {
		isActive = true;
		mBallX = x;
		mBallY = y;
	}
}
