package main;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

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
    private FrameGrabber grabber;
    private CvMemStorage storage;
    private int width, height;

    private CanvasFrame frame;

    /**
     * Initialize BoardReader.
     *
     * Creates a FrameGrabber and ...?
     * */
    public BoardReader() throws Exception {
        // from Demo.java:
        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // create a default grabber for the webcam device no. 1
        grabber = FrameGrabber.createDefault(1);
        grabber.start();

        // storage, if needed (?)
        storage = CvMemStorage.create();

        // get width and height of the image produced with grabber
        // (from Demo)
        IplImage im = grabber.grab();
        width  = im.width();
        height = im.height();
        // ??? try these:
        //grabber.getImageWidth();
        //grabber.getImageHeight();

        frame = new CanvasFrame("Tic Tac Toe Board Reader test",
                CanvasFrame.getDefaultGamma()/grabber.getGamma());


    }

    /**
     * Find the board.
     *
     * @return if a board was successfully recognized from a frame given by Grabber.
     */
    public boolean findBoard() {
        // first try finding the bounding box
        try {
            IplImage image = grabber.grab();
        } catch(FrameGrabber.Exception e){
            System.out.println("FrameGrabber exception; couldn't find a board");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // ??
    public void run() throws FrameGrabber.Exception {
        // loop: show grayed image on the canvas
        IplImage grayImage  = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        IplImage grabbedImage;

        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {
            cvClearMemStorage(storage);

            // easier to work with grayscale images
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);

            frame.showImage(grayImage);
        }
    }

    /**
     * Dispose frame, stop grabber etc.
     */
    public void closeReader() {
        // quit
        frame.dispose();
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            System.out.println("Couldn't stop FrameGrabber!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.print("trying to construct reader...");
        BoardReader reader = new BoardReader();
        System.out.println("ok!");
        System.out.print("trying to find a board...");
        boolean boardFound = reader.findBoard();
        System.out.println("finished, found:" + boardFound);
        //reader.run();
        System.out.println("closing...");
        reader.closeReader();
    }
}
