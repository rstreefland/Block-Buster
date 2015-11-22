package uk.ac.rdg.rhys.blockbuster;

//Other parts of the android libraries that we use
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * <h1>LevelBuilder.java</h1>
 * <p>
 * This class is designed to allow the user to build their own levels in an
 * easy, graphical manner. It saves each level created by the user in a separate
 * file. The file represents the name of the level. Levels created with the
 * level builder can be played in both single player and multiplayer game modes.
 * 
 * @author Rhys Streefland
 * @version 1.0
 * @since 2015-03-17
 * @see GameView GameThread
 */
public class LevelCrafter extends GameThread {

	// local Context and GameView objects
	private Context mContext;
	private GameView mGameView;

	// bitmaps used for level builder
	private Images mImages;

	// objects for the level builder interface
	private ArrayList<Block> mBlocks = new ArrayList<Block>();
	private Paint mPaint = new Paint();
	private Paint mBorderPaint = new Paint();
	private Rect mRect;

	// stores the dimensions of a cell in the grid
	private float mCellWidth;
	private float mCellHeight;

	// current block to place
	private String mCurrentBlocks;
	 
	// blocks placed by user
	private ArrayList<Block> mUserBlocks = new ArrayList<Block>();

	// This is run before anything else, so we can prepare things here
	public LevelCrafter(GameView gameView) {
		// House keeping
		super(gameView);

		// GUI draw properties
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(3);

		mGameView = gameView;

		mImages = new Images(mGameView);
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning(Context mContext) {

		// make the context accessible locally
		this.mContext = mContext;

		// blank the current block selector
		mCurrentBlocks = "";

		// set the width and height of the cells based on screen size
		mCellWidth = mCanvasWidth / 7;
		mCellHeight = mCanvasHeight / 12 + 10;

		// scale all bitmaps to the right size
		mImages.scale(mCellWidth, mCellHeight);

		// create black box for the GUI
		mRect = new Rect(0, (int) (mCanvasHeight - mCellHeight), mCanvasWidth,
				mCanvasHeight); // int left, int top, int right, int bottom

		// create one of each block for the block selection palette
		mBlocks.add(new BlockTnt(0, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockIce(1, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockStone(2, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockWood(3, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockDiamond(4, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockLava(5, 0, mCellWidth, mCellHeight));
		mBlocks.add(new BlockCraftingTable(6, 0, mCellWidth, mCellHeight));

		// adjust the position of blocks
		for (int i = 0; i < mBlocks.size(); i++) {
			mBlocks.get(i).mBlockY = mCanvasHeight - mCellHeight / 2;
		}

	}

	@Override
	protected void doDraw(Canvas canvas) {
		if (canvas == null)
			return;

		super.doDraw(canvas);

		// draw vertical line for grid
		for (int i = 1; i <= 8; i++) {
			canvas.drawLine(mCellWidth * i, 0, mCellWidth * i, (mCellHeight * 6),
					mBorderPaint); // drawLine(startx, starty, stopx, stopy);

		}

		// draw horizontal line for grid
		for (int i = 1; i <= 6; i++) {
			canvas.drawLine(0, mCellHeight * i, mCanvasWidth, mCellHeight * i,
					mBorderPaint); // drawLine(startx, starty, stopx, stopy);

		}

		// draw the black box
		canvas.drawRect(mRect, mPaint);

		// draw each block for the selection palette
		for (int i = 0; i < mBlocks.size(); i++) {
			switch (mBlocks.get(i).type) {
			case "tnt":
				canvas.drawBitmap(mImages.blockTnt, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "ice":
				canvas.drawBitmap(mImages.blockIce, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "stone":
				canvas.drawBitmap(mImages.blockStone, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "wood":
				canvas.drawBitmap(mImages.blockWood, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "diamond":
				canvas.drawBitmap(mImages.blockDiamond, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "lava":
				canvas.drawBitmap(mImages.blockLava, mBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "craftingtable":
				canvas.drawBitmap(mImages.blockCraftingTable,
						mBlocks.get(i).mBlockX - mImages.blockWidth / 2,
						mBlocks.get(i).mBlockY - mImages.blockHeight / 2, null);
				break;
			default:
				break;
			}
		}

		// draw each block placed by the user
		for (int i = 0; i < mUserBlocks.size(); i++) {
			switch (mUserBlocks.get(i).type) {
			case "tnt":
				canvas.drawBitmap(mImages.blockTnt, mUserBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mUserBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "ice":
				canvas.drawBitmap(mImages.blockIce, mUserBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mUserBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "stone":
				canvas.drawBitmap(mImages.blockStone, mUserBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mUserBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "wood":
				canvas.drawBitmap(mImages.blockWood, mUserBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mUserBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "diamond":
				canvas.drawBitmap(mImages.blockDiamond,
						mUserBlocks.get(i).mBlockX - mImages.blockWidth / 2,
						mUserBlocks.get(i).mBlockY - mImages.blockHeight / 2,
						null);
				break;
			case "lava":
				canvas.drawBitmap(mImages.blockLava, mUserBlocks.get(i).mBlockX
						- mImages.blockWidth / 2, mUserBlocks.get(i).mBlockY
						- mImages.blockHeight / 2, null);
				break;
			case "craftingtable":
				canvas.drawBitmap(mImages.blockCraftingTable,
						mUserBlocks.get(i).mBlockX - mImages.blockWidth / 2,
						mUserBlocks.get(i).mBlockY - mImages.blockHeight / 2,
						null);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void actionOnTouch(float x, float y) {

		// correct the y coordinate (wrong by default)
		float tempY = (float) (y - (mCellHeight * 1.4));

		// get the user's block selection
		for (int i = 0; i < mBlocks.size(); i++) {
			Block block = mBlocks.get(i);
			if (x < block.mBlockX + mImages.blockWidth / 2
					&& x > block.mBlockX - mImages.blockWidth / 2
					&& tempY < block.mBlockY + mImages.blockWidth / 2
					&& tempY > block.mBlockY - mImages.blockWidth / 2) {
				mCurrentBlocks = block.type;
			}
		}

		// limit the amount of unneeded processing
		if (y < mCellHeight * 7.2) {

			// convert grid representation to actual coordinates
			int blockX = (int) Math.ceil(x / mCellWidth);
			int blockY = (int) Math.ceil(tempY / mCellHeight);

			blockX--;
			blockY--;

			// if user places a block on top of another
			for (int i = 0; i < mUserBlocks.size(); i++) {
				// remove the previous block
				if (blockX == mUserBlocks.get(i).mBlockCellX
						&& blockY == mUserBlocks.get(i).mBlockCellY) {
					mUserBlocks.remove(i);
				}
			}

			// add each block selected by the user to the arraylist
			switch (mCurrentBlocks) {
			case "tnt":
				mUserBlocks.add(new BlockTnt(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "ice":
				mUserBlocks.add(new BlockIce(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "stone":
				mUserBlocks.add(new BlockStone(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "wood":
				mUserBlocks.add(new BlockWood(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "diamond":
				mUserBlocks.add(new BlockDiamond(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "lava":
				mUserBlocks.add(new BlockLava(blockX, blockY, mCellWidth,
						mCellHeight));
				break;
			case "craftingtable":
				mUserBlocks.add(new BlockCraftingTable(blockX, blockY,
						mCellWidth, mCellHeight));
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void saveLevel(String levelName) {

		String FILENAME = levelName;
		String line = "";

		FileOutputStream fos;
		try {
			// open the output file
			fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);

			// write the properties each block to a new line in the file
			for (int i = 0; i < mUserBlocks.size(); i++) {
				line = mUserBlocks.get(i).type + ","
						+ mUserBlocks.get(i).mBlockCellX + ","
						+ mUserBlocks.get(i).mBlockCellY + "\n";
				fos.write(line.getBytes());
			}

			// close file
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void updateGame(float secondsElapsed) {
		// no game logic needed here
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
