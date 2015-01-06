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

    public BoardReader() {
        // constructor
    }

    /**
     * Recognize the tic tac toe board in webcam video (TODO, now use test jpg picture)
     *
     * @return was the board found successfully
     */
    public boolean findBoard() {
        // test using the a still image
        // Mat image = Highgui.imread(getClass().getResource("/resources/test1.jpg").getPath());
        // ^ weird problem
        Mat image = Highgui.imread("bin/resources/test1.jpg");  // OK
        System.out.println(image.channels());

        // grayscale image will have just one channel
        //Mat grayImage = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        if (image.empty()) {
            System.out.println("error");
            return false;
        }
        Mat workImage = new Mat();
        Imgproc.cvtColor(image, workImage, Imgproc.COLOR_BGR2GRAY);

        /* the following procedure adapted from aishack tutorial
         * http://aishack.in/tutorials/sudoku-grabber-with-opencv-detection/
         * */

        // blur the image with GaussianBlur to reduce noise (and make the
        // 'background' squares of the paper slightly less pronounced)
        Imgproc.GaussianBlur(workImage, workImage, new Size(21, 21), 0);

        // adaptive threshold
        // 'extract' the bold, dark lines of the tic tac toe game area / board
        Imgproc.adaptiveThreshold(workImage, workImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // invert colors so that lines we're interested in are white (= large number, high intensity)
        Core.bitwise_not(workImage, workImage);
        Highgui.imwrite("test_work.jpg", workImage);

        // try to remove small artefacts with morph. open (= dilate(erode(img)) )
        Mat workImageOp = new Mat();
        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.erode(workImage, workImageOp, ekernel);

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
        Imgproc.dilate(workImageOp, workImageOp, dkernel);

        System.out.println("writing ...");
        Highgui.imwrite("test_work_op.jpg", workImageOp);

        // find lines
        Mat lines = new Mat();
        Imgproc.HoughLines(workImage, lines, 1, Math.PI / 180, 300);

        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        linesToPoints(lines, 0, workImage.width(), lPts, rPts);
        printPointLines(lPts, rPts, workImage, new Scalar(255, 255, 255));
        Highgui.imwrite("test_work_lines.jpg", workImage);
        // success!

        // in retrospec morph. open wasn't that useful, even if it looks slightly better
        //

        // find the the extreme lines ( = bounding box =outer grid = board limits)
        // still from AiShack tutorial (to get something to work, fast)
        // doesn't necessarily find the *best* extreme ones but good enough for starters
        // argh how do I get top, bottom, etc from a fucntion neatly...?!
        // (rho, theta) ... how I love thee, Python tuple
        double[] top = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] bottom = {Double.MIN_VALUE, Double.MIN_VALUE};
        double[] left = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] right = {Double.MIN_VALUE, Double.MIN_VALUE};
        double rightXintercept = 0;
        double leftXintercept = Double.MAX_VALUE;

        for (int i = 0; i < lines.cols(); i++) {
            double[] line = lines.get(0, i);
            double rho = line[0];
            double theta = line[1];
            double xIntercept = rho/Math.cos(theta);

            // the rho-theta normal form is useful for determining whether line
            // is horizontal / vertical
            if (theta > Math.PI / 4 && theta < 3 * Math.PI / 4) {
                // vertical case
                if (rho < top[0])
                    top = line;
                if (rho > bottom[0])
                    bottom = line;
            } else {
                // else horizontal
                if (xIntercept > rightXintercept) {
                    rightXintercept = xIntercept;
                    right = line;
                }
                if (xIntercept < leftXintercept) {
                    leftXintercept = xIntercept;
                    left = line;
                }
            }
        }
        //printLines(extLines, workImage, new Scalar(255, 50, 50));
        // awkward printing, let's see if we succeeded
        Scalar rgb = new Scalar(255, 50, 50);
        Mat colorImage = new Mat();
        Imgproc.cvtColor(workImage, colorImage, Imgproc.COLOR_GRAY2BGR);
        printLine(top[0], top[1], colorImage, rgb);
        printLine(bottom[0], bottom[1], colorImage, rgb);
        printLine(left[0], left[1], colorImage, rgb);
        printLine(right[0], right[1], colorImage, rgb);
        Highgui.imwrite("test_work_extrem.jpg", colorImage);

        // next: the intersections of the four lines we found
        // this I can do better than AI shack, I hope...
        Point topRight = findIntersection(top, right, workImage.width());
        Core.circle(colorImage, topRight, 10, new Scalar(100, 255, 80), 3);

        Point topLeft = findIntersection(top, left, workImage.width());
        Core.circle(colorImage, topLeft, 10, new Scalar(100, 255, 80), 3);

        Point botLeft = findIntersection(bottom, left, workImage.width());
        Core.circle(colorImage, botLeft, 10, new Scalar(100, 255, 80), 3);

        Point botRight = findIntersection(bottom, right, workImage.width());
        Core.circle(colorImage, botRight, 10, new Scalar(100, 255, 80), 3);

        Highgui.imwrite("test_work_inters.jpg", colorImage);
        // sort of success!
        // next: determine the inner grid line points from these points
        double botDiffX = botRight.x - botLeft.x;
        double botDiffY = botRight.y - botLeft.y;
        Point bottomA = new Point(botLeft.x + botDiffX/3, botLeft.y + botDiffY/3);
        Core.circle(colorImage, bottomA, 10, new Scalar(100, 255, 80), 3);

        Point bottomB = new Point(botLeft.x + 2*botDiffX/3, botLeft.y + 2*botDiffY/3);
        Core.circle(colorImage, bottomB, 10, new Scalar(100, 255, 80), 3);
        Highgui.imwrite("test_work_grid.jpg", colorImage);
        return false;
    }

    // line = [rho, theta]
    private Point findIntersection(double[] a, double[] b, double width) {
        Point inter = new Point();
        double ka = -1/Math.tan(a[1]);
        double ca = a[0]/Math.sin(a[1]);

        double kb = -1/Math.tan(b[1]);
        double cb = b[0]/Math.sin(b[1]);

        inter.x = (cb - ca)/(ka - kb);
        inter.y = ka * inter.x + ca;
        System.out.println(inter.x + " " + inter.y);
        return inter;
    }


    private void printLine(double rho, double theta, Mat outImage, Scalar rgb) {
        Point a = new Point();
        Point b = new Point();
        lineToPoints(rho, theta, 0, outImage.width(), a, b);
        Core.line(outImage, a, b, rgb);

    }
    private void printLines(Mat lines, Mat outImage, Scalar rgb){
        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        linesToPoints(lines, 0, outImage.width(), lPts, rPts);
        printPointLines(lPts, rPts, outImage, rgb);
    }

    // print lines (in two point form) to outImage
    private void printPointLines(List<Point> pts1, List<Point> pts2, Mat outImage, Scalar rgb) {
        for (int i = 0; i < pts1.size(); i++) {
            Core.line(outImage, pts1.get(i), pts2.get(i), rgb);
        }
    }

    private void lineToPoints(double rho, double theta, double left, double right, Point pt1, Point pt2) {
        double k = -1/Math.tan(theta);
        double c = rho/Math.sin(theta);

        pt1.x = left;
        pt1.y = c;

        pt2.x = right;
        pt2.y = k*right + c;
    }

    // TODO lPts, rPts lists as params are ugly hack -> better: return a list of pairs of Points
    private void linesToPoints(Mat lines, double left, double right, List<Point> lPts, List<Point> rPts) {
        // finds two points pt1, pt2 per line:
        // pt1 such that pt1.x = left, pt2 such that pt2.x = right
        // assume lPts, rPts empty or at least pts can be appended to them

        for (int i = 0; i < lines.cols(); i++) {
            // we need two points on the line to draw it with opencv
            // normal form
            double[] line = lines.get(0, i);
            double rho = line[0];
            double theta = line[1];

            double k = -1/Math.tan(theta);
            double c = rho/Math.sin(theta);

            Point pt1 = new Point();
            pt1.x = left;
            pt1.y = c;
            lPts.add(pt1);

            Point pt2 = new Point();
            pt2.x = right;
            pt2.y = k*right + c;
            rPts.add(pt2);
        }

    }


    public static void main(String[] args) throws Exception {
    	// load native library
        System.out.println("Load OpenCV native libs...");
    	System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        System.out.println("ok!");

        System.out.println("try loading test image");
        BoardReader breader = new BoardReader();
        breader.findBoard();
        System.out.println("ok!");

    }
}
