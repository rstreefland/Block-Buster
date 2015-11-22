package uk.ac.rdg.rhys.blockbuster;

import uk.ac.rdg.rhys.breakout.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public abstract class GameThread extends Thread {
	// Different mMode states
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;
	public static final int STATE_STOPPED = 6;

	// different view states
	public static final int VIEW_SINGLEPLAYER = 0;
	public static final int VIEW_MULTIPLAYER = 1;
	public static final int VIEW_LEVELBUILDER = 2;

	// Control variable for the mode of the game (e.g. STATE_WIN)
	protected int mMode = 1;

	// Control of the actual running inside run()
	private boolean mRun = false;

	// The surface this thread (and only this thread) writes upon
	private SurfaceHolder mSurfaceHolder;

	// the message handler to the View/Activity thread
	private Handler mHandler;

	// Android Context - this stores almost all we need to know
	private Context mContext;

	// The view
	public GameView mGameView;

	// We might want to extend this call - therefore protected
	protected int mCanvasWidth = 1;
	protected int mCanvasHeight = 1;

	// Last time we updated the game physics
	protected long mLastTime = 0;

	// background image
	protected Bitmap mBackgroundImage;

	// current score and number of lives
	protected int lives = 0;
	protected long score = 0;

	private long now;
	private float elapsed;

	public GameThread(GameView gameView) {
		mGameView = gameView;

		mSurfaceHolder = gameView.getHolder();
		mHandler = gameView.getmHandler();
		mContext = gameView.getContext();

		mBackgroundImage = BitmapFactory.decodeResource(gameView.getContext()
				.getResources(), R.drawable.background);
	}

	/*
	 * Called when app is destroyed, so not really that important here But if
	 * (later) the game involves more thread, we might need to stop a thread,
	 * and then we would need this Dare I say memory leak...
	 */
	public void cleanup() {
		this.mContext = null;
		this.mGameView = null;
		this.mHandler = null;
		this.mSurfaceHolder = null;
	}

	// Pre-begin a game
	abstract public void setupBeginning(Context mContext);

	// Starting up the game
	public void doStart() {
		synchronized (mSurfaceHolder) {

			setupBeginning(mContext);

			mLastTime = SystemClock.uptimeMillis() + 100;

			setState(STATE_RUNNING);

			setScore(0);
		}
	}

	// The thread start
	@Override
	public void run() {
		Canvas canvasRun;
		doStart();
		while (mRun) {
			canvasRun = null;
			try {
				canvasRun = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					if (mMode == STATE_RUNNING) {
						updatePhysics();
					}
					doDraw(canvasRun);
				}
			} finally {
				if (canvasRun != null) {
					if (mSurfaceHolder != null)
						mSurfaceHolder.unlockCanvasAndPost(canvasRun);
				}
			}
		}
	}

	/*
	 * Surfaces and drawing
	 */
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;

			// don't forget to resize the background image
			mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
					width, height, true);
		}
	}

	protected void doDraw(Canvas canvas) {

		if (canvas == null)
			return;

		if (mBackgroundImage != null)
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);
	}

	private void updatePhysics() {
		now = SystemClock.uptimeMillis();
		elapsed = (now - mLastTime) / 1000.0f;

		updateGame(elapsed);

		mLastTime = now;
	}

	abstract protected void updateGame(float secondsElapsed);

	protected void saveLevel(String levelName) {
	}

	/*
	 * Control functions
	 */

	// Finger touches the screen
	public boolean onTouch(MotionEvent e) {

		// when the user drags their finger across the screen
		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			synchronized (mSurfaceHolder) {

				// when the game is a multiplayer game handle two touches
				if (mGameView.currentView == VIEW_MULTIPLAYER) {

					// top half of the screen
					if (e.getY(0) < mCanvasWidth / 1.5) {
						this.actionOnTouch(e.getX(0), e.getY(0));
					} else {
						this.actionOnSecondTouch(e.getX(0), e.getY(0));
					}

					// if there is more than one finger touching the screen
					if (e.getPointerCount() > 1) {
						// bottom half of the screen
						if (e.getY(1) > mCanvasWidth / 1.5) {
							this.actionOnSecondTouch(e.getX(1), e.getY(1));
						} else {
							this.actionOnTouch(e.getX(1), e.getY(1));
						}
					}

					// handle one touch only
				} else {
					this.actionOnTouch(e.getRawX(), e.getRawY());
				}
			}
		}
		
		if (e.getAction() != MotionEvent.ACTION_DOWN)
			return false;

		if (mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN
				|| mMode == STATE_STOPPED) {
			doStart();
			return true;
		}

		if (mMode == STATE_PAUSE) {
			unpause();
			return true;
		}

		// when user touches the screen
		if (mGameView.currentView != VIEW_MULTIPLAYER) {
			synchronized (mSurfaceHolder) {
				this.actionOnTouch(e.getRawX(), e.getRawY());
			}
		}

		return false;
	}

	// for single and multi player games
	protected void actionOnTouch(float x, float y) {
		// Override to do something
	}

	// for multi player games only
	protected void actionOnSecondTouch(float x, float y) {
		// Override to do something
	}

	/*
	 * Game states
	 */
	public void pause() {
		synchronized (mSurfaceHolder) {
			if (mMode == STATE_RUNNING) {

				setState(STATE_PAUSE);

				if (mGameView.currentView != VIEW_LEVELBUILDER) {

					// create dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mGameView.getContext());

					// add resume button to dialog
					builder.setPositiveButton("Resume",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									unpause();
								}
							});

					// set dialog title and message
					builder.setMessage("Press the button to resume the game.")
							.setTitle("Paused");

					// show dialogs
					AlertDialog dialog = builder.create();
					dialog.show();

					System.gc(); // Run garbage collector now to clean unused
									// objects before game is resumed
				}
			}
		}
	}

	public void unpause() {
		// Move the real time clock up to now
		synchronized (mSurfaceHolder) {
			mLastTime = SystemClock.uptimeMillis();
		}
		setState(STATE_RUNNING);
	}

	// Send messages to View/Activity thread
	public void setState(int mode) {
		synchronized (mSurfaceHolder) {
			setState(mode, null);
		}
	}

	public void setState(int mode, CharSequence message) {
		synchronized (mSurfaceHolder) {
			mMode = mode;

			if (mGameView.currentView == VIEW_SINGLEPLAYER
					|| mGameView.currentView == VIEW_MULTIPLAYER) {
				if (mMode == STATE_RUNNING) {
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", "");
					msg.setData(b);
					mHandler.sendMessage(msg);
				} else {
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();

					Resources res = mContext.getResources();
					CharSequence str = "";
					if (mMode == STATE_LOSE)
						str = res.getText(R.string.mode_lose);
					else if (mMode == STATE_WIN) {
						str = res.getText(R.string.mode_win);
					}

					if (message != null) {
						str = message + "\n" + str;
					}

					b.putString("text", str.toString());

					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}
	}

	/*
	 * Getter and setter
	 */
	public void setSurfaceHolder(SurfaceHolder h) {
		mSurfaceHolder = h;
	}

	public boolean isRunning() {
		return mRun;
	}

	public void setRunning(boolean running) {
		mRun = running;
	}

	public int getMode() {
		return mMode;
	}

	public void setMode(int mMode) {
		this.mMode = mMode;
	}

	/* SCORES AND LIVES */

	// Send a score to the View
	public void setScore(long score) {
		if (mGameView.currentView == VIEW_SINGLEPLAYER
				|| mGameView.currentView == VIEW_MULTIPLAYER) {
			this.score = score;

			synchronized (mSurfaceHolder) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putBoolean("score", true);
				b.putString("text", getScoreString().toString());
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}

	}

	// Send lives to the View
	public void setLives(int lives) {
		if (mGameView.currentView == VIEW_SINGLEPLAYER
				|| mGameView.currentView == VIEW_MULTIPLAYER) {
			this.lives = lives;

			synchronized (mSurfaceHolder) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putBoolean("lives", true);
				b.putString("text", getLivesString().toString());
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}

	}

	public float getScore() {
		return score;
	}

	public void updateScore(long score) {
		this.setScore(this.score + score);
	}

	public void updateLives(int lives) {
		this.setLives(lives);
	}

	protected CharSequence getScoreString() {
		return Long.toString(Math.round(this.score));
	}
	
	protected CharSequence getLivesString() {
		return Integer.toString(Math.round(this.lives));
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