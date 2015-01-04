package main;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * From JavaCV samples, test if this builds...
 */
public class Smoother {
    public static void smooth(String filename) {
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image);
            cvSaveImage(filename, image);
            cvReleaseImage(image);
            System.out.println("ok!");
        } else {
        	System.out.println("fail");
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing JavaCV smoothing...");
        String filepath = "bin/resources/test1.jpg";
        System.out.println(filepath);
        smooth(filepath);
    }
}
