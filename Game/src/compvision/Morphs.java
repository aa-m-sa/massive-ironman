package compvision;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Morphs {

    public static Mat morphOpen(Mat src) {
        Mat dest = new Mat();
        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11));
        Imgproc.erode(src, dest, ekernel);

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(dest, dest, dkernel);
        return dest;

    }

    public static Mat morphClose(Mat src) {
        Mat dest = new Mat();

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(src, dest, dkernel);

        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.erode(dest, dest, ekernel);

        return dest;

    }

    public static Mat dilated(Mat src, int size) {
        Mat dkernel = new Mat();
        Mat dest = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(size, size));
        Imgproc.dilate(src, dest, dkernel);
        return dest;
    }



}
