package compvision;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.VideoCapture;
import org.opencv.utils.Converters;

import compvision.Grid;
import compvision.Morphs;
import compvision.lineutils.LineGroup;
import compvision.lineutils.Lines;
import game.domain.GameMove;

/**
 * Tic Tac Toe game board vision functionality.
 *
 * Must call findBaseBoard() first to find the initial board.
 * Compare the original image to the new board images with readBoardChanges.
 *
 * How it works:
 *
 *  1. find the outer lines (extreme horizontal + vertical lines) of the grid
 *  from the original image provided (image processing + morphology + Hough
 *  transform)
 *
 *  2. use the 'extreme lines' to determine the corners of game board; create a
 *  perspective corrected image, where the cells in the board grid can be
 *  determined easily with 'linear interpolation' ( = cell dist 1/3rd the
 *  distance between the corners; this is done by the Grid abstraction).
 *
 *  3. the Grid abstraction also provides the methods to compare is there's
 *  been changes in the grid cells.
 *
 *  Sans the grid structure, BoardReader is agnostic to the actual game
 *  mechanics of Tic Tac Toe; it only reports if it has detected a change the
 *  between originalImage and newImage
 *
 *  Got the idea from aishack:
 *  http://aishack.in/tutorials/sudoku-grabber-with-opencv-detection/

 *
 */
public class BoardReader {

    private Mat originalImage;
    private Mat newImage;			// the image to compare originalImage against

    private Grid boardGrid;

    // boardImages:
    // perspective corrected, cropped, morph'd, etc images of the game board
    private Mat baseBoardImage;
    private Mat newBoardImage;
    private double baseBoardWidth;   // width of persp. corrected & cropped board im

    // the extreme lines and their intersection points int the source image
    // lines in the rho-theta form
    private double[][] exLines;     // top, bottom, left, right
    private Point topRight;
    private Point topLeft;
    private Point botLeft;
    private Point botRight;

    public BoardReader(Mat image) {
        System.out.println("init BoardReader");
        this.originalImage = image;
    }

    /**
     * Replace the original with the previously loaded newImage.
     */
    public void updateBaseBoard() {
        originalImage = newImage;
        findBaseBoard();
    }

    /**
     * Replace the original with a specified newImage.
     */
    public void updateBaseBoard(Mat newImage) throws Exception {
    	setNewBoardImage(newImage);
        updateBaseBoard();
    }
    /**
     * Find the base tic tac toe board in the originalImage.
     */
    public void findBaseBoard() {
        try {
            Mat sourceImage = getWorkImage(originalImage);
            findBoardPoints(sourceImage);
            this.baseBoardImage = createBase(morphWorkImage(sourceImage));

            this.boardGrid = new Grid(baseBoardImage, getBaseBoardPoints());
            boardGrid.printPoints(baseBoardImage);
            Highgui.imwrite("test_work_grid.jpg", baseBoardImage);
        } catch(Exception e){
            System.out.println("couldn' find base board");
            e.printStackTrace();
        }
    }


    /**
     * Set the new image (to which base is compared)
     */
    private void setNewBoardImage(Mat image) throws Exception {
        this.newImage = image;
        Mat workim = getWorkImage(image);
        Mat morphed  = morphWorkImage(workim);
        this.newBoardImage = createBase(morphed);
        Highgui.imwrite("test_new_board.jpg", newBoardImage);
    }

    /**
     * Try to find if any cell in the board has changed relative to the given image.
     *
     * @return if a change was detected
     */
    public boolean readBoardChanges(Mat image) {
        try {
            setNewBoardImage(image);
            return readBoardChanges();
        } catch (Exception e) {
            System.out.println("reading changes failed");
            e.printStackTrace();
            return false;
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
        boolean changed = this.boardGrid.findChanges(newBoardImage);
        if (changed) {
            int[] c = boardGrid.getLastChangedCell();
            System.out.println("Read a change: " + c[0] + " " + c[1]);
            //this.updateBaseBoard();
        } else {
            System.out.println("no change");
        }
        return changed;
    }

    /**
     * Get the last changed cell in the board as a GameMove.
     *
     * @return a GameMove where the Mark is Empty and coordinates point to the
     * cell changed.
     */
    public GameMove getLastBoardChange() {
        int[] gridCell = boardGrid.getLastChangedCell();
        return new GameMove(gridCell[0], gridCell[1]);
    }


    // try to find a visual object resembling a tic tac toe board from originalImage
    private void findBoardPoints(Mat originalImage) throws Exception {
        Mat workImage = new Mat();
        originalImage.copyTo(workImage);

        /* the following procedure adapted from aishack tutorial
         * http://aishack.in/tutorials/sudoku-grabber-with-opencv-detection/
         */

        workImage = morphWorkImage(workImage);

        if (workImage.empty())
            throw new Exception("can't find boardpts from empty image!");

        Highgui.imwrite("test_workim.jpg", workImage);
        // find all lines from wokrImage
        Mat allLines = new Mat();
        Imgproc.HoughLines(workImage, allLines, 1, Math.PI / 90, 120);
        if (allLines.empty()) {
            throw new Exception("wtf Hough");
        }
        if (allLines.cols() < 4) {
            // couldn't find enough lines to determine a board
            throw new Exception("can't find enough lines");
        }
        System.out.println("test merging");
        Mat lines = mergeCloseLines(allLines);

        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        Lines.linesToPoints(lines, 0, workImage.width(), lPts, rPts);
        Lines.printPointLines(lPts, rPts, workImage, new Scalar(255, 255, 255));
        Highgui.imwrite("test_work_lines.jpg", workImage);

        // next:
        // find the the extreme lines ( = bounding box =outer grid = board limits)
        // doesn't necessarily find the *best* lines; but good enough for starters

        exLines  = findExtremeLines(lines);
        if (exLines == null)
            throw new Exception("didn't find exlines");

        // printing, let's see if we succeeded
        printExtremeLines(workImage, exLines);

        // next: find the intersections of the four lines we found
        topRight = Lines.findIntersection(exLines[0], exLines[3], workImage.width());
        topLeft = Lines.findIntersection(exLines[0], exLines[2], workImage.width());
        botLeft = Lines.findIntersection(exLines[1], exLines[2], workImage.width());
        botRight = Lines.findIntersection(exLines[1], exLines[3], workImage.width());

    }


    // create a base image (that Grid can use) from the originalImage
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
            if (theta > Math.PI * 80 / 180 && theta <  Math.PI * 100 / 180 ) {
                // line was horizontal enough, see if it's our best guess for top
                // line or bottom line
                if (rho < top[0]) {
                    top = line;
                    topFound = true;
                }
                if (rho > bottom[0]) {
                    bottom = line;
                    bottomFound = true;
                }
            } else if (theta < Math.PI * 20 / 180 || theta > Math.PI * 160 / 180) {
                // similarly for the vertical (left / right) case
                // except use the coordinate where the line intercepts the X
                // axis the determine how far left /right it is
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

    // the hackiest hack of all hacks
    // in other words, quite ugly way of doing this.
    private Mat mergeCloseLines(Mat lines) {
        // group all lines
        // first by theta, then lines with similar theta by rho
        List<LineGroup> thetaGroups = new ArrayList<LineGroup>();

        // first case is special
        double[] line = lines.get(0, 0);
        double rho = line[0];
        double theta = line[1];
        thetaGroups.add(new LineGroup(rho, theta));

        double thetaDist = Math.PI * 15 / 180;
        double rhoDist = 30;

        // group by theta
        // compare each line to the already found groups, and either put it
        // into one of them *or* create a new group for it (if it doesn't fit
        // any of them)
        for (int i = 1; i < lines.cols(); i++) {
            line = lines.get(0, i);
            rho = line[0];
            theta = line[1];
            //System.out.println(rho + " " + theta);
            // try to fit line into a group by theta
            int bestGroup = -1;
            double bestDist = Double.MAX_VALUE;
            // the comparision
            for (int j = 0; j < thetaGroups.size(); j++) {
                double curDist = Math.abs(thetaGroups.get(j).getThetaMean() - theta);
                if (curDist < thetaDist && curDist <= bestDist) {
                    bestGroup = j;
                }
            }
            // if -1 -> no good group; else -> add to the best group
            if (bestGroup == -1) {
                thetaGroups.add(new LineGroup(rho,  theta));
            } else {
                thetaGroups.get(bestGroup).addLine(rho, theta);
            }
        }

        // divide each thetaGroup to groups by rho
        // similar to theta grouping, bu now done for each thetaGroup by rho
        // TODO refactor into something more sensible -> code reusability?
        List<LineGroup> finalGroups = new ArrayList<LineGroup>();
        for (int i = 0; i < thetaGroups.size(); i++) {
            LineGroup lg = thetaGroups.get(i);
            List<LineGroup> rhoGroups = new ArrayList<LineGroup>();
            rhoGroups.add(new LineGroup(lg.getLineRho(0), lg.getLineTheta(0)));
            for (int j = 1; j < lg.size(); j++) {
                double temptheta = lg.getLineTheta(j);
                double temprho = lg.getLineRho(j);
                int bestGroup = -1;
                double bestDist = Double.MAX_VALUE;
                for (int k = 0; k < rhoGroups.size(); k++) {
                    double cur = Math.abs(rhoGroups.get(k).getRhoMean() - temprho);
                    if (cur < rhoDist && cur <= bestDist) {
                        bestGroup = k;
                    }
                }
                if (bestGroup == -1) {
                    rhoGroups.add(new LineGroup(temprho,  temptheta));
                } else {
                    rhoGroups.get(bestGroup).addLine(temprho, temptheta);
                }
            }
            finalGroups.addAll(rhoGroups);
        }


        Mat merged = new Mat(1, finalGroups.size(), lines.type());
        for (int i = 0; i < finalGroups.size(); i++) {
            double[] mergedLine = new double[2];
            mergedLine[0] = finalGroups.get(i).getRhoMean();
            mergedLine[1] = finalGroups.get(i).getThetaMean();
            merged.put(0, i, mergedLine);
            //System.out.println(mergedLine[0] + " " + mergedLine[1]);
        }

        System.out.println("merged " + lines.cols() + "  lines into " + finalGroups.size());


        //System.out.println(merged.size());
        return merged;
    }

    private void printExtremeLines(Mat image, double[][] exLines) {
        Scalar rgb1 = new Scalar(255, 50, 50);
        Scalar rgb2 = new Scalar(0, 0, 255);
        Mat colorImage = new Mat();
        Imgproc.cvtColor(image, colorImage, Imgproc.COLOR_GRAY2BGR);
        Lines.printLine(exLines[0][0], exLines[0][1], colorImage, rgb1);
        Lines.printLine(exLines[1][0], exLines[1][1], colorImage, rgb1);
        Lines.printLine(exLines[2][0], exLines[2][1], colorImage, rgb2);
        Lines.printLine(exLines[3][0], exLines[3][1], colorImage, rgb2);
        Highgui.imwrite("test_work_extrem.jpg", colorImage);
    }

    private Mat loadImageFromFile(String path) {
        Mat image = Highgui.imread(path);  // OK

        if (image.empty()) {
            System.out.println("error");
            throw new IllegalArgumentException("Couldn't read image from " + path);
        }
        return image;
    }

    private Mat getWorkImage(Mat image) throws Exception {
        if (image.empty())
            throw new Exception("can't work on empty image");
        Mat workImage = new Mat();
        Imgproc.cvtColor(image, workImage, Imgproc.COLOR_BGR2GRAY);
        if (workImage.empty())
            throw new Exception("creating workimage failed");
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

        // thresold 'breaks' some thin lines in the image, try to retrieve them
        // with dilation and morph close
        //dst = Morphs.dilated(dst, 3);
        dst = Morphs.morphClose(dst, 3, 3);
        return dst;
    }


    // a main for testing
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // load native library
        System.out.println("Load OpenCV native libs...");
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        System.out.println("ok!");


        //BoardReader breader = new BoardReader();

        scanner.close();
    }

}
