package penbot;

import java.lang.Math;

/**
 * Board
 * 
 * Board and board cell representation. Mainly angle + distance information the
 * PenBot needs to move the pen tip above the center of each cell
 * 
 * Board x,y coordinate legend: 2,2|1,2|0,2 ----------- 1,2|1,1|0,1 -----------
 * 0,2|0,1|0,0 (bot)
 * 
 * 0 degree angle is the x == y diagonal
 * 
 */
public class Board {
    private double outerCellSize;
    private double penDist;
    private double[][] cellAngles = new double[3][3];
    private double[][] cellDists = new double[3][3];
    
    public Board(double outerCellSize, double penDist) {
        this.outerCellSize = outerCellSize;
        this.penDist = penDist;
        
        initAngles();
        initDists();
    }

    public double getCellTargetAngle(int x, int y) {
        if (x < 0 || y < 0 || x > 2 || y > 2) {
            throw new IllegalArgumentException("illegal cell index");
        }
        return cellAngles[x][y];
    }

    public double getCellTargetDist(int x, int y) {
        if (x < 0 || y < 0 || x > 2 || y > 2) {
            throw new IllegalArgumentException("illegal cell index");
        }
        return cellDists[x][y];
    }
    
    private void initAngles() {
        // shorthand for readable math formulas
        double s = outerCellSize;
        double d = penDist;
        // cells (1,0), (0,1)
        double angle = Math.atan(s/(2*s + Math.sqrt(2)*d));
        cellAngles[1][0] = -angle;
        cellAngles[0][1] = angle;
        // cells (2,0), (0,2)
        angle = Math.atan((Math.sqrt(2)*s) / (3*s/Math.sqrt(2) + d));
        cellAngles[2][0] = -angle;
        cellAngles[0][2] = angle;
        // cells (2,1), (1,2)
        angle = Math.atan(s/(4*s + Math.sqrt(2)*d));
        cellAngles[2][1] = -angle;
        cellAngles[1][2] = angle;
        // rest
        cellAngles[0][0] = 0;
        cellAngles[1][1] = 0;
        cellAngles[2][2] = 0;
    }
    
    private void initDists() {
        // shorthand for readable math formulas
        double s = outerCellSize;
        double d = penDist;
        // easy ones: 0,0; 1,1; 2,2
        cellDists[0][0] = s/Math.sqrt(2);
        cellDists[1][1] = 3*s/Math.sqrt(2);
        cellDists[2][2] = 5*s/Math.sqrt(2);
        // 1,0;0,1
        // h = hypetenuse, a, b helpers
        double h = calcDist(d + s*Math.sqrt(2), s/Math.sqrt(2));
        cellDists[1][0] = h;
        cellDists[0][1] = h;
        // 2,0;0,2
        h = calcDist(d + 3*s/Math.sqrt(2), Math.sqrt(2)*s);
        cellDists[2][0] = h;
        cellDists[0][2] = h;
        // 2,1;1,2
        h = calcDist(d + 2*Math.sqrt(2)*s, s/Math.sqrt(2));
        cellDists[2][1] = h;
        cellDists[1][2] = h;
    }
    
    private double calcDist(double a, double b) {
        return Math.sqrt(a*a + b*b) - penDist;
    }

}
