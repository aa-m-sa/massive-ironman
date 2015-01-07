package main;

import org.opencv.core.Point;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Scalar;

/**
 * Internal representation of the game board grid as a visual object.
 * */
public class Grid {
    private Point tl;
    private Point tr;
    private Point bl;
    private Point br;

    private Point[] topPts;
    private Point[] bottomPts;
    private Point[] leftPts;
    private Point[] rightPts;

    private Mat boardImage;

    /**
     * Create a Grid over a image of a game board by specifying the corner points.
     */
    public Grid(Mat boardImage, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        if (!validate(topLeft, topRight, bottomLeft, bottomRight))
            throw new IllegalArgumentException("Can't construct a Grid from these points!");
        this.tl = topLeft;
        this.tr = topRight;
        this.bl = bottomLeft;
        this.br = bottomRight;
        this.boardImage = boardImage;

        determineGridPoints();
    }

    public void printPoints(Mat outImage) {
        for (int i = 0; i < 4; i++) {
            Core.circle(outImage, topPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, bottomPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, leftPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, rightPts[i], 10, new Scalar(100, 255, 80), 3);
        }
    }

    // TODO
    private boolean validate(Point tl, Point tr, Point bl, Point br) {
        return true;
    }

    // determine the other intersection points in the Grid
    private void determineGridPoints() {
        bottomPts = getPointsOnLine(bl, br);
        topPts = getPointsOnLine(tl, tr);
        leftPts = getPointsOnLine(tl, bl);
        rightPts =  getPointsOnLine(tr, br);

    }

    // Point a should be closer to (0,0) than b
    // TODO choose a from two points
    private Point[] getPointsOnLine(Point a, Point b) {
        Point[] pts = new Point[4];
        pts[0] = a;
        pts[3] = b;
        double xDiff = b.x - a.x;
        double yDiff = b.y - a.y;
        pts[1] = new Point(a.x + xDiff/3, a.y + yDiff/3);
        pts[2] = new Point(a.x + 2*xDiff/3, a.y + 2*yDiff/3);
        return pts;
    }

}
