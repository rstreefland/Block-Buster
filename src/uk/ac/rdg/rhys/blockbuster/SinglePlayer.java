package uk.ac.rdg.rhys.blockbuster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

/**
 * <h1>SinglePlayer.java</h1>
 * <p>
 * This class is responsible for running the single player game mode. It
 * generates blocks randomly if no level file is supplied. It draws the bitmaps
 * on the screen and performs the game logic.
 * 
 * @author Rhys Streefland
 * @version 1.0
 * @since 2015-03-17
 * @see GameView GameThread
 */
public class SinglePlayer extends GameThread {

	// local Context and GameView objects
	private Context mContext;
	private GameView mGameView;

	// game objects
	private Ball mBalls[];
	private Block mBlocks[];
	private Paddle mPaddle;

	// bitmaps used in the game
	private Images mImages;

	// dimensions of a grid cell
	private float mCellWidth;
	private float mCellHeight;

	// initial speed of balls
	private float mInitialSpeed;

	// ball counters
	private int mNumberOfBalls;
	private int mActiveBalls;

	// block counters
	private int mNumberOfBlocks;
	private int mBlocksRemaining;

	// number of lives
	private int mLives;

	// timer for fire balls
	private float mEndTimer;

	// minimum distances for collision detection
	private float mMinDistanceBetweenBallAndPaddle = 0;
	private float mMinDistanceBetweenBallAndblock = 0;

	public SinglePlayer(GameView gameView) {
		// House keeping
		super(gameView);

		mGameView = gameView;

		// set up game bitmaps
		mImages = new Images(mGameView);
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning(Context mContext) {

		// make the context accessible locally
		this.mContext = mContext;

		// set the number of mLives to 3
		mLives = 3;
		updateLives(mLives);

		// set the width and height of the cells based on screen size
		mCellWidth = mCanvasWidth / 7;
		mCellHeight = mCanvasHeight / 12 + 10;

		// scale all bitmaps to the right size
		mImages.scale(mCellWidth, mCellHeight);

		// create all three balls but make only one active
		mActiveBalls = 1;
		mNumberOfBalls = 3;
		mBalls = new Ball[mNumberOfBalls];
		mBalls[0] = new Ball(mGameView, true, mCellWidth, mCellHeight);
		mBalls[1] = new Ball(mGameView, false, mCellWidth, mCellHeight);
		mBalls[2] = new Ball(mGameView, false, mCellWidth, mCellHeight);

		// set the position of the first ball
		mBalls[0].mBallX = mCanvasWidth / 2;
		mBalls[0].mBallY = (float) (mCanvasHeight / 1.5);

		// calculate ball speed based on screen dpi
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		mInitialSpeed = (float) (metrics.densityDpi * 1.8);

		// set ball speeds
		mBalls[0].mBallSpeedX = mInitialSpeed;
		mBalls[0].mBallSpeedY = mInitialSpeed;
		mBalls[1].mBallSpeedX = mInitialSpeed;
		mBalls[1].mBallSpeedY = mInitialSpeed;
		mBalls[2].mBallSpeedX = -mInitialSpeed;
		mBalls[2].mBallSpeedY = -mInitialSpeed;

		// create a paddle and set its properties
		mPaddle = new Paddle();
		mPaddle.mPaddleX = mCanvasWidth / 2;
		mPaddle.mPaddleY = mCanvasHeight - mCanvasHeight / 17;
		mPaddle.mPaddleSpeedX = 0;

		// determine if level should be randomly generated or loaded from file
		if (mGameView.currentLevelName != "") {
			levelFromFile();
		} else {
			randomLevel();
		}

		// calculate collision detection distances
		mMinDistanceBetweenBallAndPaddle = (mImages.paddleWidth / 2 + mImages.ballWidth / 2)
				* (mImages.paddleWidth / 2 + mImages.ballWidth / 2);

		mMinDistanceBetweenBallAndblock = (mImages.blockWidth / 2 + mImages.ballWidth / 2)
				* (mImages.blockWidth / 2 + mImages.ballWidth / 2);

		System.gc(); // Run garbage collector now (before the game loop starts
						// to reduce lag)
	}

	public void randomLevel() {

		// set number of blocks randomly
		Random rand = new Random();
		mNumberOfBlocks = rand.nextInt(42 - 5) + 5;
		mBlocks = new Block[mNumberOfBlocks];

		// initialise temporary variables
		int blockType = -1;
		int blockX = -1;
		int blockY = -1;
		int currentNumber = 0;
		boolean place = false;

		// place each block
		for (int i = 0; i < mNumberOfBlocks; i++) {
			blockType = rand.nextInt(8 - 1) + 1;

			// always place the first block
			if (currentNumber == 0) {
				blockX = rand.nextInt(7 - 0) + 0;
				blockY = rand.nextInt(6 - 0) + 0;
				place = true;
			} else {
				place = false;
			}

			// don't place a block in the same position as another block
			while (place == false) {
				blockX = rand.nextInt(7 - 0) + 0;
				blockY = rand.nextInt(6 - 0) + 0;

				for (int j = 0; j < currentNumber; j++) {
					if (mBlocks[j].mBlockCellX == blockX
							&& mBlocks[j].mBlockCellY == blockY) {
						place = false;
						break;

					} else {
						place = true;
					}
				}
			}

			// create the block
			switch (blockType) {
			case 1:
				mBlocks[i] = new BlockTnt(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 2:
				mBlocks[i] = new BlockIce(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 3:
				mBlocks[i] = new BlockStone(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 4:
				mBlocks[i] = new BlockWood(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 5:
				mBlocks[i] = new BlockDiamond(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 6:
				mBlocks[i] = new BlockLava(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			case 7:
				mBlocks[i] = new BlockCraftingTable(blockX, blockY, mCellWidth,
						mCellHeight);
				break;
			}

			currentNumber++;
		}

	}

	public void levelFromFile() {
		// attempt to load a level from the level file
		try {
			BufferedReader inputReader = new BufferedReader(
					new InputStreamReader(
							mContext.openFileInput(mGameView.currentLevelName)));
			String inputString;
			StringBuffer stringBuffer = new StringBuffer();

			// read each line of the file
			while ((inputString = inputReader.readLine()) != null) {
				stringBuffer.append(inputString + "\n");
			}

			// split the file into an array of lines (each line represents a
			// block)
			String[] lines = stringBuffer.toString().split("[\\r\\n]+");
			mNumberOfBlocks = lines.length;

			mBlocks = new Block[mNumberOfBlocks];

			// create each block
			for (int i = 0; i < mNumberOfBlocks; i++) {

				// each attribute of the block is separated by a commma
				String[] attributes = lines[i].split(",");

				switch (attributes[0]) {
				case "tnt":
					mBlocks[i] = new BlockTnt(Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "ice":
					mBlocks[i] = new BlockIce(Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "stone":
					mBlocks[i] = new BlockStone(
							Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "wood":
					mBlocks[i] = new BlockWood(Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "diamond":
					mBlocks[i] = new BlockDiamond(
							Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "lava":
					mBlocks[i] = new BlockLava(Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				case "craftingtable":
					mBlocks[i] = new BlockCraftingTable(
							Integer.parseInt(attributes[1]),
							Integer.parseInt(attributes[2]), mCellWidth,
							mCellHeight);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doDraw(Canvas canvas) {

		if (canvas == null)
			return;

		super.doDraw(canvas);

		// draw each active ball
		for (Ball ball : mBalls) {
			if (ball.isActive == true) {
				switch (ball.type) {
				case 0:
					canvas.drawBitmap(mImages.mBall, ball.mBallX
							- mImages.ballWidth / 2, ball.mBallY
							- mImages.ballHeight / 2, null);
					break;
				case 1:
					canvas.drawBitmap(mImages.fireBall, ball.mBallX
							- mImages.ballWidth / 2, ball.mBallY
							- mImages.ballHeight / 2, null);
					break;
				case 2:
					canvas.drawBitmap(mImages.iceBall, ball.mBallX
							- mImages.ballWidth / 2, ball.mBallY
							- mImages.ballHeight / 2, null);
					break;
				default:
					break;
				}
			}
		}

		// draw the paddle
		canvas.drawBitmap(mImages.mPaddle, mPaddle.mPaddleX
				- mImages.paddleWidth / 2, mPaddle.mPaddleY
				- mImages.paddleHeight / 2, null);

		// draw each active block
		for (Block block : mBlocks) {
			if (block.isActive == true) {

				switch (block.type) {
				case "tnt":
					canvas.drawBitmap(mImages.blockTnt, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "ice":
					canvas.drawBitmap(mImages.blockIce, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "stone":
					canvas.drawBitmap(mImages.blockStone, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "wood":
					canvas.drawBitmap(mImages.blockWood, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "diamond":
					canvas.drawBitmap(mImages.blockDiamond, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "lava":
					canvas.drawBitmap(mImages.blockLava, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case "craftingtable":
					canvas.drawBitmap(mImages.blockCraftingTable, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				default:
					break;
				}

				// if the block is cracked - overlay the crack images
				switch (block.crack) {
				case 1:
					canvas.drawBitmap(mImages.cracked1, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				case 2:
					canvas.drawBitmap(mImages.cracked2, block.mBlockX
							- mImages.blockWidth / 2, block.mBlockY
							- mImages.blockHeight / 2, null);
					break;
				default:
					break;
				}

			}
		}
	}

	@Override
	protected void actionOnTouch(float x, float y) {

		// set paddle to touch location
		mPaddle.mPaddleX = x;

		// keep paddle from going off screen - left
		if (mPaddle.mPaddleX < mImages.paddleWidth / 2)
			mPaddle.mPaddleX = mImages.paddleWidth / 2;

		// keep paddle from going off screen - right
		if (mPaddle.mPaddleX > mCanvasWidth - mImages.paddleWidth / 2)
			mPaddle.mPaddleX = mCanvasWidth - mImages.paddleWidth / 2;
	}

	// perform the game logic
	@Override
	protected void updateGame(float secondsElapsed) {

		// run the game logic for each ball
		for (Ball ball : mBalls) {

			// a fire ball becomes a normal ball after 5 seconds
			mEndTimer = (System.currentTimeMillis() - ball.startTimer) / 1000.0f;
			if (ball.type == 1 && mEndTimer >= 5) {
				ball.type = 0;
				ball.startTimer = 0;
			}

			// If the ball moves down on the screen
			if (ball.isActive == true) {
				if (ball.mBallSpeedY > 0) {
					// Check for a paddle collision
					detectCollision(ball, false, mPaddle.mPaddleX,
							mCanvasHeight, mMinDistanceBetweenBallAndPaddle);
				}

				// Move the ball's X and Y using the speed (pixel/sec)
				ball.mBallX = ball.mBallX + secondsElapsed * ball.mBallSpeedX;
				ball.mBallY = ball.mBallY + secondsElapsed * ball.mBallSpeedY;

				// Check if the ball hits either the left side or the right side
				// of
				// the
				// screen
				// But only do something if the ball is moving towards that side
				// of
				// the
				// screen
				// If it does that => change the direction of the ball in the X
				// direction
				if ((ball.mBallX <= mImages.ballWidth / 2 && ball.mBallSpeedX < 0)
						|| (ball.mBallX >= mCanvasWidth - mImages.ballWidth / 2 && ball.mBallSpeedX > 0)) {
					ball.mBallSpeedX = -ball.mBallSpeedX;
				}

				// keep count of the remaining number of blocks
				mBlocksRemaining = mNumberOfBlocks;
				for (Block block : mBlocks) {
					if (block.isActive == false) {
						mBlocksRemaining--;
					}

					// if the block is active - check for a collision between it
					// and the current ball
					if (block.isActive == true) {
						if (detectCollision(ball, true, block.mBlockX,
								block.mBlockY, mMinDistanceBetweenBallAndblock)) {
							updateBlock(block, ball);
						}
					}
				}

				// if there are no blocks left, the user has won
				if (mBlocksRemaining == 0) {
					setState(GameThread.STATE_WIN);
				}

				// If the ball goes out of the top of the screen and moves
				// towards
				// the
				// top of the screen =>
				// change the direction of the ball in the Y direction
				if (ball.mBallY <= mImages.ballWidth / 2
						&& ball.mBallSpeedY < 0) {
					ball.mBallSpeedY = -ball.mBallSpeedY;
				}

				// If all of the balls go out of the bottom of the screen lose a
				// life
				// until the number of lives reaches zero
				if (ball.mBallY >= mCanvasHeight) {
					if (mActiveBalls > 1) {
						mActiveBalls--;
						ball.isActive = false;
					} else if (mLives > 1) {
						mBalls[0].makeActive(mCanvasWidth / 2,
								(float) (mCanvasHeight / 1.5));
						mLives--;
						updateLives(mLives);
					} else {
						setState(GameThread.STATE_LOSE);
					}
				}

			}
		}

	}

	public int updateBlock(Block block, Ball ball) {

		// decrement the block's hit counter if the block has hits remaining
		// or if the current ball is a fire ball
		if (block.hits > 1 && ball.type != 1) {
			block.hits--;

			// crack the ball if it is not fully cracked
			if (block.crack != 2) {
				block.crack++;
			}

			// remove a block if its been hit the correct number of times and
			// perform the block's special function
		} else {

			switch (block.type) {
			// LAVA: make the ball a fire ball
			case "lava":
				ball.type = 1;
				ball.mBallSpeedX = (float) (mInitialSpeed / 1.2);
				ball.mBallSpeedY = (float) (mInitialSpeed / 1.2);
				ball.startTimer = System.currentTimeMillis();
				break;
			// ICE: make the ice an ice ball
			case "ice":
				ball.type = 2;
				ball.mBallSpeedX = (float) (mInitialSpeed * 1.2);
				ball.mBallSpeedY = (float) (mInitialSpeed * 1.2);
				break;
			// TNT: 'explode' all blocks in a one block radius
			case "tnt":
				int blockCellX = block.mBlockCellX;
				int blockCellY = block.mBlockCellY;

				for (Block block2 : mBlocks) {
					if (blockCellX - 1 == block2.mBlockCellX
							&& blockCellY == block2.mBlockCellY
							|| blockCellX + 1 == block2.mBlockCellX
							&& blockCellY == block2.mBlockCellY
							|| blockCellX - 1 == block2.mBlockCellX
							&& blockCellY + 1 == block2.mBlockCellY
							|| blockCellX == block2.mBlockCellX
							&& blockCellY + 1 == block2.mBlockCellY
							|| blockCellX + 1 == block2.mBlockCellX
							&& blockCellY + 1 == block2.mBlockCellY
							|| blockCellX - 1 == block2.mBlockCellX
							&& blockCellY - 1 == block2.mBlockCellY
							|| blockCellX == block2.mBlockCellX
							&& blockCellY - 1 == block2.mBlockCellY
							|| blockCellX + 1 == block2.mBlockCellX
							&& blockCellY - 1 == block2.mBlockCellY) {
						block2.isActive = false;
						updateScore(1);
					}
				}
				break;
			// CRAFTING TABLE: make a maximum of two balls active so there are
			// three
			// balls on the screen
			case "craftingtable":
				float blockX = block.mBlockX;
				float blockY = block.mBlockY;

				if (mBalls[0].isActive == false) {
					mBalls[0].makeActive(blockX, blockY);
					mActiveBalls++;
				}

				if (mBalls[1].isActive == false) {
					mBalls[1].makeActive(blockX, blockY);
					mActiveBalls++;
				}

				if (mBalls[2].isActive == false) {
					mBalls[2].makeActive(blockX, blockY);
					mActiveBalls++;
				}
				break;
			default:
				break;
			}

			updateScore(1);
			block.isActive = false;
			return 1;
		}
		return 0;
	}

	// collision detection between ball and paddle or ball and block
	private boolean detectCollision(Ball ball, Boolean isBlock, float x,
			float y, float minDistance) {

		// calculate current distance
		float distanceBetweenBallAndObject = (x - ball.mBallX)
				* (x - ball.mBallX) + (y - ball.mBallY) * (y - ball.mBallY);

		// Check if the actual distance is lower than the allowed => collision
		if (minDistance >= distanceBetweenBallAndObject) {

			// Get the present velocity (this should also be the velocity going
			// away after the collision)
			float velocityOfBall = (float) Math.sqrt(ball.mBallSpeedX
					* ball.mBallSpeedX + ball.mBallSpeedY * ball.mBallSpeedY);

			// Change the direction of the ball if it's not a fire ball
			if (ball.type == 1 && isBlock == true) {
			} else {
				ball.mBallSpeedX = ball.mBallX - x;
				ball.mBallSpeedY = ball.mBallY - y;
			}

			// Get the velocity after the collision
			float newVelocity = (float) Math.sqrt(ball.mBallSpeedX
					* ball.mBallSpeedX + ball.mBallSpeedY * ball.mBallSpeedY);

			// using the fraction between the original velocity and present
			// velocity to calculate the needed
			// speeds in X and Y to get the original velocity but with the new
			// angle.
			ball.mBallSpeedX = ball.mBallSpeedX * velocityOfBall / newVelocity;
			ball.mBallSpeedY = ball.mBallSpeedY * velocityOfBall / newVelocity;

			return true;
		}

		return false;
	}
}

// This file is part of the course
// "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it. If not, see <http://www.gnu.org/licenses/>.
