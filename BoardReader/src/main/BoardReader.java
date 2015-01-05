package main;

import org.opencv.core.Core;
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
        // ???
        //System.out.println(getClass().getResource("/resources/test1.jpg"));
        // ^ oikea polku tiedostoon, printtaus onnistuu
        // mutta
        //Mat image = Highgui.imread(getClass().getResource("/resources/test1.jpg").getPath());
        // luo tyhjän imagen
        // sen sijaan
        Mat image = Highgui.imread("bin/resources/test1.jpg");  // toimii
        System.out.println(image.channels());

        // grayscale image will have just one channel
        //Mat grayImage = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        if (image.empty()) {
            System.out.println("error");
            return false;
        }
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // blur the image with GaussianBlur to get rid of noise etc
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(21, 21), 0);

        System.out.println("writing blurred grayscale..");
        Highgui.imwrite("test_blur_gray.jpg", grayImage);

        return false;
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
