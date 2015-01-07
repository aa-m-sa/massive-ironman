package boardreader;
// TODO change package -> utils

import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;


/**
 * Static utility methods for line goemetry
 */
public class Lines {
    /**
     * Return the intersection point of aLine and bLine.
     * Line format a double[2] array where line[0] = rho and line[1] = theta.
     * TODO currently unsafe (division by zero possible!), doesn't handle parallel lines!
     */
    public static Point findIntersection(double[] aLine, double[] bLine, double width) {
        Point inter = new Point();
        double ka = -1/Math.tan(aLine[1]);
        double ca = aLine[0]/Math.sin(aLine[1]);

        double kb = -1/Math.tan(bLine[1]);
        double cb = bLine[0]/Math.sin(bLine[1]);

        inter.x = (cb - ca)/(ka - kb);
        inter.y = ka * inter.x + ca;
        System.out.println(inter.x + " " + inter.y);
        return inter;
    }

    /**
     * Prints a rho-theta line to outImage
     */
    public static void printLine(double rho, double theta, Mat outImage, Scalar rgb) {
        Point a = new Point();
        Point b = new Point();
        lineToPoints(rho, theta, 0, outImage.width(), a, b);
        Core.line(outImage, a, b, rgb);

    }

    /**
     * Prints multiple rho-theta lines (given as a Mat) to outImage
     */
    public static void printLines(Mat lines, Mat outImage, Scalar rgb){
        List<Point> lPts = new ArrayList<Point>();
        List<Point> rPts = new ArrayList<Point>();
        linesToPoints(lines, 0, outImage.width(), lPts, rPts);
        printPointLines(lPts, rPts, outImage, rgb);
    }

    /**
     * Print lines (each given by two points) to outImage.
     */
    public static void printPointLines(List<Point> pts1, List<Point> pts2, Mat outImage, Scalar rgb) {
        for (int i = 0; i < pts1.size(); i++) {
            Core.line(outImage, pts1.get(i), pts2.get(i), rgb);
        }
    }

    /**
     * Get two points on a rho-theta line, one at horizontal coordinate 'left' and other 'right'.
     * TODO return points instead of modifying the Points given as params? (compare linesToPoints below)
     */
    public static void lineToPoints(double rho, double theta, double left, double right, Point pt1, Point pt2) {
        double k = -1/Math.tan(theta);
        double c = rho/Math.sin(theta);

        pt1.x = left;
        pt1.y = c;

        pt2.x = right;
        pt2.y = k*right + c;
    }

    /**
     * Same as lineToPoints, but for a Mat of rho-theta lines.
     */
    // TODO lPts, rPts lists as params are ugly hack -> better: return a list of pairs of Points (?)
    public static void linesToPoints(Mat lines, double left, double right, List<Point> lPts, List<Point> rPts) {
        // finds two points pt1, pt2 per line:
        // pt1 such that pt1.x = left, pt2 such that pt2.x = right
        // assume lPts, rPts empty or at least pts can be appended to them

        for (int i = 0; i < lines.cols(); i++) {
            // we need two points on the line to draw it with opencv
            // normal form
            double[] line = lines.get(0, i);
            double rho = line[0];
            double theta = line[1];

            Point pt1 = new Point();
            Point pt2 = new Point();

            lineToPoints(rho, theta, left, right, pt1, pt2);
            lPts.add(pt1);
            rPts.add(pt2);
        }
    }

}
