package uk.ac.rdg.rhys.blockbuster;


public class BlockDiamond extends Block {

	public BlockDiamond(int x, int y, float cellWidth, float cellHeight) {
		type = "diamond";
		hits = 10;
		isActive = true;
		
		
		
		mBlockCellX = x;
		mBlockCellY = y;
		x++;
		y++;
		mBlockX = (float) ((cellWidth * x) - (cellWidth / 2));
		mBlockY = (float) ((cellHeight * y) - (cellHeight / 2));
	}
}
