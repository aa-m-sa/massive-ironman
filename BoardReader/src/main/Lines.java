package main;

import org.opencv.core.Point;
// TODO change package -> utils


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

}
