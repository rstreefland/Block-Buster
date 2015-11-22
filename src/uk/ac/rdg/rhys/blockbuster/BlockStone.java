package uk.ac.rdg.rhys.blockbuster;


public class BlockStone extends Block {

	public BlockStone(int x, int y, float cellWidth, float cellHeight) {
		type = "stone";
		hits = 5;
		isActive = true;
		
		
		
		mBlockCellX = x;
		mBlockCellY = y;
		x++;
		y++;
		mBlockX = (float) ((cellWidth * x) - (cellWidth / 2));
		mBlockY = (float) ((cellHeight * y) - (cellHeight / 2));

	}
}
