package main;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

/**
 * The actual attempt at Tic Tac Toe Board reading.
 *
 * TODO: when able to read the board, implement in the Game
 */
public class BoardReader {
    public static void main(String[] args) throws Exception {
        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // create a default grabber for the webcam device no. 1
        FrameGrabber grabber = FrameGrabber.createDefault(1);
        grabber.start();

        // storage, if needed (?)
        CvMemStorage storage = CvMemStorage.create();

        IplImage grabbedImage = grabber.grab();
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        // CanvasFrame webcam output; gamma correction as in Demo.java
        CanvasFrame frame = new CanvasFrame("Tic Tac Toe Board Reader test",
                CanvasFrame.getDefaultGamma()/grabber.getGamma());

        // loop
        // let's first test canvas, webcam grabbing 'n stuff
        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {
            cvClearMemStorage(storage);

            // easier to work with grayscale images
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);

            frame.showImage(grayImage);
        }

        // quit
        frame.dispose();
        grabber.stop();
    }
}
