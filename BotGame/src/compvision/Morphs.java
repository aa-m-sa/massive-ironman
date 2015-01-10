package compvision;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Morphs {

    public static Mat morphOpen(Mat src, int ero, int dil) {
        Mat dest = new Mat();
        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(ero, ero));
        Imgproc.erode(src, dest, ekernel);

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(dil, dil));
        Imgproc.dilate(dest, dest, dkernel);
        return dest;

    }

    public static Mat morphClose(Mat src, int dil, int ero) {
        Mat dest = new Mat();

        Mat dkernel = new Mat();
        dkernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(dil, dil));
        Imgproc.dilate(src, dest, dkernel);

        Mat ekernel = new Mat();
        ekernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(ero, ero));
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
