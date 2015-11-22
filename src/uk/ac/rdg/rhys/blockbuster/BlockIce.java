package uk.ac.rdg.rhys.blockbuster;


public class BlockIce extends Block {

	public BlockIce(int x, int y, float cellWidth,
			float cellHeight) {
		type = "ice";
		hits = 1;
		isActive = true;

		

		mBlockCellX = x;
		mBlockCellY = y;
		x++;
		y++;
		mBlockX = (float) ((cellWidth * x) - (cellWidth / 2));
		mBlockY = (float) ((cellHeight * y) - (cellHeight / 2));
	}
}
