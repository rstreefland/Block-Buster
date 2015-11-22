package uk.ac.rdg.rhys.blockbuster;


public class BlockTnt extends Block {

	public BlockTnt(int x, int y, float cellWidth, float cellHeight) {
		type = "tnt";
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
