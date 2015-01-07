package main;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import main.Grid;
import main.Lines;

/**
 * The actual attempt at Tic Tac Toe Board reading.
 *
 * The Idea:
 *  - when the cam has been set up, grab a frame (or if more finesse is
 *  preferred, a bunch of frame and choose best / average) and find the tic tac
 *  toe board
 *  - by 'find' we mean: determine the outer lines of the box
 *  - then recognize the box: find / try to fit the inner lines (= that divide
 *  the board box in to cells) and thus find the board cells
 *  - game hasn't started yet, so the cells are *empty*; their current contents
 *  are thus background noise
 *  - now we're ready for the main 'reading' part itself:
 *  - when the Game is waiting for the player input, start:
 *  - the loop: compare if there has been a significant change in the contents of
 *  any cell relative to all other cells; if such a change seems to persist,
 *  most likely that means a X/O mark has been drawn in that cell
 *  - when there's significant disturbance (human player's hand / pen, Penbot
 *  is on the board drawing its own mark, etc), discard the webcam input
 *  - a) do not confuse the human opponent's and Penbot's marks! or
 *  - b) report all new drawn marks, it's not BoardReader's job to determine
 *  whose they are (the game knows whose turn it is anyway, so it can infer who
 *  did draw it)
 *
 * TODO: when able to read the board, combine this project with the Game
 */
public class BoardReader {
    private Grid boardGrid;
    private Mat origGray;
    private Mat baseBoardImage;

    // extreme lines of original board image
    // rho theta
    private double[] top    = {Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] bottom = {Double.MIN_VALUE, Double.MIN_VALUE};
    private double[] left   = {Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] right  = {Double.MIN_VALUE, Double.MIN_VALUE};

    public BoardReader() {
        // constructor
    }

    /**
     * Recognize the tic tac toe board in webcam video (TODO, now use test jpg
     * picture) and store it's features as a Grid object.
     *
     * @return was the board found successfully
     */
    public boolean findBoard() {
        //TODO webcam
        Mat workImage = getWorkImage("bin/resources/test1.jpg");
        origGray = new Mat();
        workImage.copyTo(origGray);

        /* the following procedure adapted from aishack tutorial
         * http://aishack.in/tutorials/sudoku-grabber-with-opencv-detection/
         * */

        // blur the image with GaussianBlur to reduce noise (and make the
        // 'background' squares of the paper slightly less pronounced)
        Imgproc.GaussianBlur(workImage, workImage, new Size(21, 21), 0);

        // adaptive threshold
        // 'extract' the bold, dark lines of the tic tac toe game area / board
        Imgproc.adaptiveThreshold(workImage, workImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // invert colors so that the lines we're interested in are white (= large number, high intensity)
        Core.bitwise_not(workImage, workImage);
        Highgui.imwrite("test_work.jpg", workImage);

        // try to remove small artefacts with morph. open (= dilate(erode(img)) )
        Mat workImageOp = new Mat();
        morphOpen(workImage, workImageOp);

        Highgui.imwrite("test_work_op.jpg", workImageOp);

        // find all lines from wokrImage
        Mat lines = new Mat();
        Imgproc.HoughLines(workImage, lines, 1, Math.PI / 180, 300);
        if (lines.cols() < 4) {
            // couldn't find enough lines to determine a board -> return false
            return false;
        }

        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        Lines.linesToPoints(lines, 0, workImage.width(), lPts, rPts);
        Lines.printPointLines(lPts, rPts, workImage, new Scalar(255, 255, 255));
        Highgui.imwrite("test_work_lines.jpg", workImage);
        // success!

        // in retrospec morph. open wasn't that useful, even if it looks slightly better

        // next:
        // find the the extreme lines ( = bounding box =outer grid = board limits)
        // still from AiShack tutorial (to get something to work, fast)
        // doesn't necessarily find the *best* extreme ones but good enough for starters
        // argh how would I get top, bottom, etc returned from a function neatly...?!

        boolean linesFound = findExtremeLines(lines);
        if (!linesFound)
            return false;

        // printing, let's see if we succeeded
        Scalar rgb = new Scalar(255, 50, 50);
        Mat colorImage = new Mat();
        Imgproc.cvtColor(workImage, colorImage, Imgproc.COLOR_GRAY2BGR);
        Lines.printLine(top[0], top[1], colorImage, rgb);
        Lines.printLine(bottom[0], bottom[1], colorImage, rgb);
        Lines.printLine(left[0], left[1], colorImage, rgb);
        Lines.printLine(right[0], right[1], colorImage, rgb);
        Highgui.imwrite("test_work_extrem.jpg", colorImage);

        // next: find the intersections of the four lines we found
        // this I can do better than AI shack...
        Point topRight = Lines.findIntersection(top, right, workImage.width());
        Point topLeft = Lines.findIntersection(top, left, workImage.width());
        Point botLeft = Lines.findIntersection(bottom, left, workImage.width());
        Point botRight = Lines.findIntersection(bottom, right, workImage.width());

        createGridAndBase(topLeft, topRight, botRight, botLeft);
        return true;
    }

    private void createGridAndBase(Point topLeft, Point topRight, Point botRight, Point botLeft) {
        // base image's side == board bottom side
        double dx = botLeft.x - botRight.x;
        double dy = botLeft.y - botLeft.y;
        double maxLen = Math.sqrt(dx * dx + dy * dy);

        List<Point> srcPts = new ArrayList<Point>();
        srcPts.add(topLeft);
        srcPts.add(topRight);
        srcPts.add(botRight);
        srcPts.add(botLeft);

        Point newTopLeft = new Point(0,0);
        Point newTopRight = new Point(maxLen - 1, 0);
        Point newBotRight = new Point(maxLen - 1, maxLen - 1);
        Point newBotLeft = new Point(0, maxLen - 1);

        List<Point> targetPts = new ArrayList<Point>();
        targetPts.add(newTopLeft);
        targetPts.add(newTopRight);
        targetPts.add(newBotRight);
        targetPts.add(newBotLeft);

        baseBoardImage = perspectiveCorrected(srcPts, targetPts, maxLen);
        Highgui.imwrite("test_persp.jpg", baseBoardImage);

        this.boardGrid = new Grid(baseBoardImage, newTopLeft, newTopRight, newBotRight, newBotLeft);
        boardGrid.printPoints(baseBoardImage);
        Highgui.imwrite("test_work_grid.jpg", baseBoardImage);
    }

    // find the board edges (== extreme lines) from all lines
    private boolean findExtremeLines(Mat lines) {
        // another hackish solution
        boolean topFound, bottomFound, leftFound, rightFound;
        topFound = bottomFound = leftFound = rightFound = false;
        double rightXintercept = 0;
        double leftXintercept = Double.MAX_VALUE;

        for (int i = 0; i < lines.cols(); i++) {
            double[] line = lines.get(0, i);
            double rho = line[0];
            double theta = line[1];
            double xIntercept = rho/Math.cos(theta);

            // the rho-theta normal form is useful for determining whether line
            // is horizontal / vertical
            if (theta > Math.PI * 3 / 8 && theta <  Math.PI * 5 / 8 ) {
                // vertical case
                if (rho < top[0]) {
                    top = line;
                    topFound = true;
                }
                if (rho > bottom[0]) {
                    bottom = line;
                    bottomFound = true;
                }
            } else if (theta < Math.PI * 1 / 8 || theta > Math.PI * 7 / 8) {
                // else horizontal
                if (xIntercept > rightXintercept) {
                    rightXintercept = xIntercept;
                    right = line;
                    rightFound = true;
                }
                if (xIntercept < leftXintercept) {
                    leftXintercept = xIntercept;
                    left = line;
                    leftFound = true;
                }
            }
        }
        if (!topFound || !bottomFound || !leftFound || !rightFound) {
            return false;
        }

        return true;
    }

    // for testing:
    // get work image from a specified file
    private Mat getWorkImage(String path) {
        // test using the a still image
        Mat image = Highgui.imread(path);  // OK
        // TODO use webcam feed

        if (image.empty()) {
            System.out.println("error");
            throw new IllegalArgumentException("Couldn't read image from " + path);
        }
        Mat workImage = new Mat();
        Imgproc.cvtColor(image, workImage, Imgproc.COLOR_BGR2GRAY);
        return workImage;

    }

    // use the board corner points to create a perspective corrected (and
    // cropped) image from the original grayscale image
    private Mat perspectiveCorrected(List<Point> srcPts, List<Point> targetPts, double width) {

        Mat src = Converters.vector_Point2f_to_Mat(srcPts);

        Mat target = Converters.vector_Point2f_to_Mat(targetPts);

        Mat corrected = new Mat();
        Imgproc.warpPerspective(origGray, corrected,
                Imgproc.getPerspectiveTransform(src, target), new Size(width, width));
        return corrected;
    }

    private static void morphOpen(Mat src, Mat dest) {
        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.erode(src, dest, ekernel);

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
        Imgproc.dilate(dest, dest, dkernel);

    }



    public static void main(String[] args) throws Exception {
    	// load native library
        System.out.println("Load OpenCV native libs...");
    	System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        System.out.println("ok!");

        System.out.println("try loading test image");
        BoardReader breader = new BoardReader();
        boolean success = breader.findBoard();
        System.out.println(success);
        System.out.println("ok!");

    }
}
