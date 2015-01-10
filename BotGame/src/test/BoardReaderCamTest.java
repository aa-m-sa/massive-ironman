package test;

import game.domain.GameMove;

import java.util.Scanner;
// no proper unit testing,
// rather a 'script' for testing BoardReader with the webcam







import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import compvision.BoardReader;
import compvision.Webcam;

public class BoardReaderCamTest {
    private Webcam webcam;
    BoardReader breader;

    public BoardReaderCamTest() {
        webcam = new Webcam(1);
        Thread camThread = new Thread(webcam);
        camThread.start();
        // set up
        Mat initIm;
        while (webcam.getGrab().empty()) {
            try {
                Thread.sleep(50);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        initIm = webcam.getGrab();
        Highgui.imwrite("init-test.jpg", initIm);
        this.breader = new BoardReader(initIm);
        breader.findBaseBoard();
    }

    public void readChange() {

        Mat newGrab = webcam.getGrab();

        if (breader.readBoardChanges(newGrab)) {
            GameMove possible = breader.getLastBoardChange();
            System.out.println("Found change: " + possible);
        } else {
            System.out.println("No changes detected");
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        BoardReaderCamTest tester = new BoardReaderCamTest();
        Scanner reader = new Scanner(System.in);
        String inp = reader.nextLine();
        while (!inp.equals("quit")) {
            tester.readChange();
            inp = reader.nextLine();
            if (inp.equals("u"))
                tester.breader.updateBaseBoard();
        }
    }

}
