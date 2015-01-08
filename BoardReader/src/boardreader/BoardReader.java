package boardreader;

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

import boardreader.Grid;
import boardreader.Lines;
import boardreader.Morphs;

/**
 * The actual attempt at Tic Tac Toe Board reading.
 *
 * The Mighty Plan:
 *  - (assuming webcam up and running), grab a frame (or if more finesse is
 *  preferred, a bunch of frame and choose best / average) and find the tic tac
 *  toe board
 *  - by 'find' we mean: determine the outer lines of the box
 *  - then recognize the box: find / try to fit the inner lines (= that divide
 *  the board box in to cells) and thus find the board cells
 *  - game hasn't started yet, so the cells are *empty*; their current contents
 *  are thus background noise
 *  - now we're ready for the main 'reading' part itself:
 *  - when the Game is waiting for the player input, start:
 *  - the loop: compare if there has been a significant change,
 *  most likely that means a X/O mark has been drawn in that cell
 *  - significant change = L2 norm compared to previous cell
 *  (no time for more intelligent heurestics)
 *  - when there's significant disturbance (human player's hand / pen, Penbot
 *  is on the board drawing its own mark, etc), discard the webcam input
 *  - significant disturbance == significant histogram change in (color) image?
 *  - report all new drawn marks, it's not BoardReader's job to determine
 *  whose they are (the game knows whose turn it is anyway, so it can infer who
 *  did draw it)
 *
 * TODO: when able to read the board, combine this project with the Game
 */
public class BoardReader {
    // TODO: instead of using still image as a source, use webcam
    private String sourceImagePath;
    private Grid boardGrid;

    private Mat baseBoardImage;
    private Mat newBoardImage;
    private double baseBoardWidth;   // width of persp. corrected & cropped board im

    // the extreme lines and their intersection points int the source image
    private double[][] exLines;     // top, bottom, left, right
    private Point topRight;
    private Point topLeft;
    private Point botLeft;
    private Point botRight;

    public BoardReader(String imagePath) {
        // constructor
        this.sourceImagePath = imagePath;
    }

    public void setSourceImagePath(String newPath) {
        this.sourceImagePath = newPath;
    }

    /**
     * Find the base tic tac toe board from source image.
     */
    public void findBaseBoard() {
        try {
            //this.origGray = getWorkImage();
            this.baseBoardImage = findBoard(sourceImagePath);
            this.boardGrid = new Grid(baseBoardImage, getBaseBoardPoints());
            boardGrid.printPoints(baseBoardImage);
            Highgui.imwrite("test_work_grid.jpg", baseBoardImage);
        } catch (Exception e) {
            System.out.println("couldn't find board");
        }
    }

    public void setNewBoardImage(String path) {
        try {
            // okay this was a bad idea
            // this.newBoardImage = findBoard(path);
            // reuse original base points
            Mat im = getWorkImage(path);
            Mat mim  = morphWorkImage(im);
            this.newBoardImage = createBase(mim);
            Highgui.imwrite("test_new_board.jpg", newBoardImage);
        } catch (Exception e) {
            System.out.println("couldn't find new board");
            System.out.println(e);
        }
    }


    /**
     * Try to find if any cell in the board has changed.
     *
     * Compares the baseBoardImage to newBoardImage
     *
     * @return if a change was detected
     */
    public boolean readBoardChanges() {

        if (this.newBoardImage == null || this.newBoardImage.empty())
            return false;
        System.out.println("trying to read changes");
        this.boardGrid.findChanges(newBoardImage);
        return false;
    }


    // try to find a visual object resembling a tic tac toe board from image
    // specified by the path
    private Mat findBoard(String path) throws Exception {
        Mat workImage = getWorkImage(path);
        Mat originalImage = workImage.clone();

        /* the following procedure adapted from aishack tutorial
         * http://aishack.in/tutorials/sudoku-grabber-with-opencv-detection/
         */

        workImage = morphWorkImage(workImage);

        // find all lines from wokrImage
        Mat lines = new Mat();
        Imgproc.HoughLines(workImage, lines, 1, Math.PI / 180, 350);
        if (lines.cols() < 4) {
            // couldn't find enough lines to determine a board
            throw new Exception("can't find enough lines");
        }

        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        Lines.linesToPoints(lines, 0, workImage.width(), lPts, rPts);
        Lines.printPointLines(lPts, rPts, workImage, new Scalar(255, 255, 255));
        Highgui.imwrite("test_work_lines.jpg", workImage);
        // success!

        // next:
        // find the the extreme lines ( = bounding box =outer grid = board limits)
        // also adapted from the AiShack tutorial (to get something to work, fast)
        // doesn't necessarily find the *best* lines; but good enough for starters

        exLines  = findExtremeLines(lines);

        // printing, let's see if we succeeded
        printExtremeLines(workImage, exLines);

        // next: find the intersections of the four lines we found
        // this I can do better than AI shack...
        topRight = Lines.findIntersection(exLines[0], exLines[3], workImage.width());
        topLeft = Lines.findIntersection(exLines[0], exLines[2], workImage.width());
        botLeft = Lines.findIntersection(exLines[1], exLines[2], workImage.width());
        botRight = Lines.findIntersection(exLines[1], exLines[3], workImage.width());

        return createBase(morphWorkImage(originalImage));
    }


    private Mat createBase(Mat originalImage) {

        // base image's side == board bottom side

        List<Point> srcPts = new ArrayList<Point>();
        srcPts.add(topLeft);
        srcPts.add(topRight);
        srcPts.add(botRight);
        srcPts.add(botLeft);


        setBaseBoardWidth(botLeft, botRight);
        return perspectiveCorrected(originalImage, srcPts, getBaseBoardPoints(), baseBoardWidth);
    }

    private static double getDist(Point botLeft, Point botRight) {
        double dx = botLeft.x - botRight.x;
        double dy = botLeft.y - botLeft.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void setBaseBoardWidth(Point botLeft, Point botRight) {
        this.baseBoardWidth = getDist(botLeft, botRight);
    }

    private List<Point> getBaseBoardPoints() {
        Point newTopLeft = new Point(0,0);
        Point newTopRight = new Point(baseBoardWidth - 1, 0);
        Point newBotRight = new Point(baseBoardWidth - 1, baseBoardWidth - 1);
        Point newBotLeft = new Point(0, baseBoardWidth - 1);

        List<Point> targetPts = new ArrayList<Point>();
        targetPts.add(newTopLeft);
        targetPts.add(newTopRight);
        targetPts.add(newBotRight);
        targetPts.add(newBotLeft);
        return targetPts;

    }

    // find the board edges (== extreme lines) from all lines
    private double[][] findExtremeLines(Mat lines) {
        // extreme lines of original board image
        // rho theta
        double[] top    = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] bottom = {Double.MIN_VALUE, Double.MIN_VALUE};
        double[] left   = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] right  = {Double.MIN_VALUE, Double.MIN_VALUE};
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
            return null;
        }

        System.out.println("extremes found");
        double[][] extremeLines = {top, bottom, left, right};
        return extremeLines;
    }

    private void printExtremeLines(Mat image, double[][] exLines) {
        Scalar rgb = new Scalar(255, 50, 50);
        Mat colorImage = new Mat();
        Imgproc.cvtColor(image, colorImage, Imgproc.COLOR_GRAY2BGR);
        Lines.printLine(exLines[0][0], exLines[0][1], colorImage, rgb);
        Lines.printLine(exLines[1][0], exLines[1][1], colorImage, rgb);
        Lines.printLine(exLines[2][0], exLines[2][1], colorImage, rgb);
        Lines.printLine(exLines[3][0], exLines[3][1], colorImage, rgb);
        Highgui.imwrite("test_work_extrem.jpg", colorImage);
    }

    // for testing:
    // get work image from a specified file
    private Mat getWorkImage() {
        return getWorkImage(sourceImagePath);
    }

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
    private Mat perspectiveCorrected(Mat srcImage, List<Point> srcPts, List<Point> targetPts, double width) {

        Mat src = Converters.vector_Point2f_to_Mat(srcPts);

        Mat target = Converters.vector_Point2f_to_Mat(targetPts);

        Mat corrected = new Mat();
        Imgproc.warpPerspective(srcImage, corrected,
                Imgproc.getPerspectiveTransform(src, target), new Size(width, width));
        return corrected;
    }

    private static Mat morphWorkImage(Mat src) {
        Mat dst = new Mat();
        // blur the image with GaussianBlur to reduce noise (and make the
        // 'background' squares of the paper slightly less pronounced)
        Imgproc.GaussianBlur(src, dst, new Size(21, 21), 0);

        // adaptive threshold: 'extract' the bold, dark lines of the tic tac
        // toe game area / board
        Imgproc.adaptiveThreshold(dst, dst, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // invert colors so that the lines we're interested in are white (= large number = high intensity)
        Core.bitwise_not(dst, dst);
        Highgui.imwrite("test_work.jpg", dst);

        // thresold 'breaks' some thin lines in the image, try to retrieve them
        // with dilation and morph close
        dst = Morphs.dilated(dst, 5);
        dst = Morphs.morphClose(dst);
        return dst;
    }


    public static void main(String[] args) throws Exception {
        // load native library
        System.out.println("Load OpenCV native libs...");
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        System.out.println("ok!");

        System.out.println("try loading test image");
        BoardReader breader = new BoardReader("bin/resources/2015-01-07-series-1.jpg");
        System.out.println("try finding base board");
        breader.findBaseBoard();
        System.out.println("ok!");
        System.out.println("try loading new test image");
        breader.setNewBoardImage("bin/resources/2015-01-07-series-1.jpg");
        breader.readBoardChanges();
        System.out.println("...");
        breader.setNewBoardImage("bin/resources/2015-01-07-series-2.jpg");
        breader.readBoardChanges();

    }
}
