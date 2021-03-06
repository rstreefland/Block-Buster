package uk.ac.rdg.rhys.blockbuster;


public class BlockWood extends Block {

	public BlockWood(int x, int y, float cellWidth, float cellHeight) {
		type = "wood";
		hits = 3;
		isActive = true;
		
		mBlockCellX = x;
		mBlockCellY = y;
		x++;
		y++;
		mBlockX = (float) ((cellWidth * x) - (cellWidth / 2));
		mBlockY = (float) ((cellHeight * y) - (cellHeight / 2));
	}
}
