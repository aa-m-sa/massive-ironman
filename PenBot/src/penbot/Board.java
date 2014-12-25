package penbot;

/**
 * Board
 * 
 * Board and board cell representation.
 * Mainly angle + distance information the PenBot needs to move
 * the pen tip above the center of each cell
 * 
 * Board x,y coordinate legend:
 * 2,2|1,2|0,2
 * -----------
 * 1,2|1,1|0,1
 * -----------
 * 0,2|0,1|0,0
 *            (bot)
 * 
 * 0 degree angle is the x == y diagonal
 *
 */
public class Board {
	
	public Board(double outerCellSize, double penDist) {
		//TODO
	}

	public double getCellTargetAngle(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getCellTargetDist(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
