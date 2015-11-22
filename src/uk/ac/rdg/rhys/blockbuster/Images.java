package uk.ac.rdg.rhys.blockbuster;

import uk.ac.rdg.rhys.breakout.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Images {
	
	public int paddleWidth;
	public int paddleHeight;
	
	public int ballWidth;
	public int ballHeight;
	
	public int blockWidth;
	public int blockHeight;

	public Bitmap mPaddle;
	public Bitmap mPaddle2;
	
	public Bitmap mBall;
	public Bitmap fireBall;
	public Bitmap iceBall;
	
	public Bitmap blockCraftingTable;
	public Bitmap blockDiamond;
	public Bitmap blockIce;
	public Bitmap blockLava;
	public Bitmap blockStone;
	public Bitmap blockTnt;
	public Bitmap blockWood;
	
	public Bitmap cracked1;
	public Bitmap cracked2;
	
	public Images(GameView mGameView) {
		mPaddle  = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.paddle);
		mPaddle2  = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.paddle2);
		
		mBall = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.ball);
		fireBall = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.ballfire);
		iceBall = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.ballice);
		
		
		blockCraftingTable = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.craftingtable);
		blockDiamond = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.diamond);
		blockIce = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.ice);
		blockLava = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.lava);
		blockStone = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.stone);
		blockTnt = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.tnt);
		blockWood = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.wood);
		
		cracked1 = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.cracked1);
		cracked2 = BitmapFactory.decodeResource(mGameView.getContext()
				.getResources(), R.drawable.cracked2);
	}
	
	public void scale(float cellWidth, float cellHeight) {
		mPaddle = Bitmap.createScaledBitmap(mPaddle, (int)(cellWidth*3/1.5), (int)(cellHeight/1.5), true);
		mPaddle2 = Bitmap.createScaledBitmap(mPaddle2, (int)(cellWidth*3/1.5), (int)(cellHeight/1.5), true);
		
		mBall = Bitmap.createScaledBitmap(mBall, (int)(cellWidth/2.5), (int)(cellHeight/2.5), true);
    	fireBall = Bitmap.createScaledBitmap(fireBall, (int)(cellWidth/2.5), (int)(cellHeight/2.5), true);
    	iceBall = Bitmap.createScaledBitmap(iceBall, (int)(cellWidth/2.5), (int)(cellHeight/2.5), true);
    	
    	blockCraftingTable = Bitmap.createScaledBitmap(blockCraftingTable, (int)cellWidth, (int)cellHeight, true);
    	blockDiamond = Bitmap.createScaledBitmap(blockDiamond, (int)cellWidth, (int)cellHeight, true);
    	blockIce = Bitmap.createScaledBitmap(blockIce, (int)cellWidth, (int)cellHeight, true);
    	blockLava = Bitmap.createScaledBitmap(blockLava, (int)cellWidth, (int)cellHeight, true);
    	blockStone = Bitmap.createScaledBitmap(blockStone, (int)cellWidth, (int)cellHeight, true);
    	blockTnt = Bitmap.createScaledBitmap(blockTnt, (int)cellWidth, (int)cellHeight, true);
    	blockWood = Bitmap.createScaledBitmap(blockWood, (int)cellWidth, (int)cellHeight, true);
    	
    	cracked1 = Bitmap.createScaledBitmap(cracked1, (int)cellWidth, (int)cellHeight, true);
    	cracked2 = Bitmap.createScaledBitmap(cracked2, (int)cellWidth, (int)cellHeight, true);
    	
    	paddleWidth = mPaddle.getWidth();
    	paddleHeight = mPaddle.getHeight();
    	
    	ballWidth = mBall.getWidth();
    	ballHeight = mBall.getHeight();
    	
    	blockWidth = blockIce.getWidth();
    	blockHeight = blockIce.getHeight();
	}	
}
