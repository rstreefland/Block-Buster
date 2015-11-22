package uk.ac.rdg.rhys.blockbuster;

public abstract class Block {

	// type of block
	String type;
	
	// number of hits block can take
	int hits;

	// position of block on screen
	public float mBlockX = 0;
	public float mBlockY = 0;

	// position of block in grid
	public int mBlockCellX = 0;
	public int mBlockCellY = 0;

	// is block active in the game
	public boolean isActive;

	// how cracked is the block (0,1,2)
	public int crack = 0;

}
