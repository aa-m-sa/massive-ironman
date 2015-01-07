package boardreader;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.opencv.highgui.Highgui;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

/**
 * Internal representation of the game board grid as a visual object.
 * */
public class Grid {
    private Point tl;
    private Point tr;
    private Point bl;
    private Point br;

    private Point[] topPts;
    private Point[] secondRowPts;
    private Point[] thirdRowPts;
    private Point[] bottomPts;
    private Point[] leftPts;
    private Point[] rightPts;


    private List<Mat> cells = new ArrayList<Mat>();
    private List<Mat> cellHistograms = new ArrayList<Mat>();

    private Mat baseImage;

    /**
     * Create a Grid over a image of a game board by specifying the corner points.
     */
    public Grid(Mat baseImage, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        if (!validate(topLeft, topRight, bottomLeft, bottomRight))
            throw new IllegalArgumentException("Can't construct a Grid from these points!");
        this.tl = topLeft;
        this.tr = topRight;
        this.bl = bottomLeft;
        this.br = bottomRight;
        this.baseImage = baseImage;

        determineGridPoints();
        makeCells();
    }

    public void printPoints(Mat outImage) {
        for (int i = 0; i < 4; i++) {
            Core.circle(outImage, topPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, bottomPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, secondRowPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, thirdRowPts[i], 10, new Scalar(100, 255, 80), 3);
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
        secondRowPts = getPointsOnLine(leftPts[1], rightPts[1]);
        thirdRowPts = getPointsOnLine(leftPts[2], rightPts[2]);

    }

    private void makeCells() {
        Point[][] cellPoints = {topPts, secondRowPts, thirdRowPts, bottomPts};
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                makeCell(cellPoints[j][i], cellPoints[j][i+1], cellPoints[j+1][i], cellPoints[j+1][i+1]);
                Highgui.imwrite("test_cell_mask" + i + j + ".jpg", cells.get(i+j*3));
            }
        }
    }

    private void makeCell(Point ul, Point ur, Point ll, Point lr) {
        // cell: 'mask away' the image outside the shape defined by points, add to list
        Mat mask = new Mat(baseImage.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
        Core.rectangle(mask, ul, lr, new Scalar(255, 255, 255), -1);
        Mat cell = new Mat();
        baseImage.copyTo(cell, mask);
        cells.add(cell);

        // also add its histogram to list
        Mat hist = new Mat();
        MatOfInt channel = new MatOfInt(0);
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat range = new MatOfFloat(0, 255);

        Imgproc.calcHist(Arrays.asList(baseImage), channel, mask, hist, histSize, range);
        cellHistograms.add(hist);
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
