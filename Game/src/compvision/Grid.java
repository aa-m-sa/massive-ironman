package compvision;

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

import compvision.BoardReader;

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

    private int[] lastChangedCell = {-1, -1};

    private List<Mat> cells = new ArrayList<Mat>();
    private List<Mat> cellHistograms = new ArrayList<Mat>();

    private Mat baseImage;

    /**
     * Create a Grid over a image of a game board by specifying the corner points.
     */
    public Grid(Mat baseImage, Point topLeft, Point topRight, Point bottomRight, Point bottomLeft) {
        if (!validate(topLeft, topRight, bottomLeft, bottomRight))
            throw new IllegalArgumentException("Can't construct a Grid from these points!");
        this.tl = topLeft;
        this.tr = topRight;
        this.bl = bottomLeft;
        this.br = bottomRight;
        this.baseImage = baseImage;

        determineGridPoints();
        makeBaseCells();
    }

    public Grid(Mat baseImage, List<Point> points) {
        // there's got to be a better way, but anyway
        this(baseImage, points.get(0), points.get(1), points.get(2), points.get(3));
    }

    public void printPoints(Mat outImage) {
        for (int i = 0; i < 4; i++) {
            Core.circle(outImage, topPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, bottomPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, secondRowPts[i], 10, new Scalar(100, 255, 80), 3);
            Core.circle(outImage, thirdRowPts[i], 10, new Scalar(100, 255, 80), 3);
        }
    }

    public boolean findChanges(Mat newImage) {
        List<Mat> newCells = new ArrayList<Mat>();
        List<Mat> newHistograms = new ArrayList<Mat>();
        makeCells(newImage, newCells, newHistograms, "new");

        System.out.println("finding changes");
        // set averages
        double histSum = 0;
        double[] histDiffs = new double[9];
        // norms taken but not currently used
        double normSum = 0;
        double[] norms = new double[9];
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                int index = i + j*3;
                // compare histograms
                double hdiff = Imgproc.compareHist(this.cellHistograms.get(index),
                        newHistograms.get(index), Imgproc.CV_COMP_CHISQR);
                //System.out.println(j + " " + i + "(" + index +")" + "\n" + hdiff);
                histSum += hdiff;
                histDiffs[index] = hdiff;
                double lnorm = Core.norm(cells.get(index), newCells.get(index), Core.NORM_L1);
                //System.out.println(lnorm);
                normSum += lnorm;
                norms[index] = hdiff;

            }
        }
        double histMean = histSum / 9;
        double normMean = normSum / 9;
        System.out.println("hist average:" + histSum/9);
        // compare to averages
        // variance and mean absolute deviation
        double mad = 0;
        double var = 0;
        int maxDiff = 0;
        int mdi, mdj;
        mdi = mdj = 0;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                int index = i + j*3;
                //System.out.println(i);
                double d = histDiffs[index] - histMean;
                if (d >= histDiffs[maxDiff]) {
                    maxDiff = index;
                    mdi = i;
                    mdj = j;
                }
                //System.out.println(d);
                var += d * d;
                mad += Math.abs(d);
            }
        }
        System.out.println("maxDiff:" + histDiffs[maxDiff]);
        var = var /9;
        mad = mad /9;
        //System.out.println("var:" + var );
        System.out.println("mad:" + mad);

        // handpicked guesstimates
        if (histDiffs[maxDiff] < 1000 && mad < 300) {
            System.out.println("no change in grid");
            return false;
        }
        this.lastChangedCell[0] = mdj;
        this.lastChangedCell[1] = mdi;
        return true;
    }

    /**
     * @return the last changed cell, in bot coordinates
     * */
    public int[] getLastChangedCell() {
        return lastChangedCell;
    }

    // TODO
    private boolean validate(Point tl, Point tr, Point bl, Point br) {
        return true;
    }

    // determine the other intersection points in the Grid
    private void determineGridPoints() {
        bottomPts = getPointsOnLine(br, bl);
        topPts = getPointsOnLine(tr, tl);
        leftPts = getPointsOnLine(tl, bl);
        rightPts =  getPointsOnLine(tr, br);
        secondRowPts = getPointsOnLine(rightPts[1], leftPts[1]);
        thirdRowPts = getPointsOnLine(rightPts[2], leftPts[2]);

    }

    private void makeBaseCells() {
        makeCells(this.baseImage, this.cells, this.cellHistograms, "base");
    }

    private void makeCells(Mat image, List<Mat> cellList, List<Mat> histList, String imStr) {
        //Point[][] cellPoints = {topPts, secondRowPts, thirdRowPts, bottomPts};
        Point[][] cellPoints = {bottomPts, thirdRowPts, secondRowPts, topPts};
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                makeCell(image, cellPoints[j][i], cellPoints[j][i+1],
                        cellPoints[j+1][i], cellPoints[j+1][i+1], cellList, histList);
                Highgui.imwrite(
                        "test_cell_" + imStr  + j + i +
                        ".jpg", cellList.get(i+j*3));
            }
        }
    }

    private void makeCell(Mat image, Point ul, Point ur, Point ll, Point lr,
            List<Mat> cellList, List<Mat> histList) {
        // cell: 'mask away' the image outside the shape defined by points, add
        // to list
        Mat mask = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
        int mb = 40;
        Point maskPt1 = new Point(ul.x + mb, ul.y + mb);
        Point maskPt2 = new Point(lr.x - mb, lr.y - mb);

        Core.rectangle(mask, maskPt1, maskPt2, new Scalar(255, 255, 255), -1);
        Mat cell = new Mat();
        image.copyTo(cell, mask);
        // morph open: get rid of small artefacts
        cell = Morphs.morphOpen(cell);
        cellList.add(cell);

        // also add its histogram to list
        Mat hist = new Mat();
        MatOfInt channel = new MatOfInt(0);
        MatOfInt histSize = new MatOfInt(64);
        MatOfFloat range = new MatOfFloat(0, 255);

        Imgproc.calcHist(Arrays.asList(image), channel, mask, hist, histSize, range);
        histList.add(hist);

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
