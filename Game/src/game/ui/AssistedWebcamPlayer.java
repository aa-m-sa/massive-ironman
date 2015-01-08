package game.ui;

import java.util.Scanner;

import org.opencv.core.Mat;

import game.Player;
import game.Mark;
import game.GameMove;
import compvision.BoardReader;
import compvision.Webcam;

/**
 * 'Assisted' webcam player: determine player moves from webcam image grabs,
 * but depends on player stdin input command to *grab* an image and when to
 * ignore bot movements.
 */
public class AssistedWebcamPlayer implements Player {

    private Scanner reader;
    private Webcam webcam;
    private BoardReader breader;
    private Mark mark;

    public AssistedWebcamPlayer(Webcam webcam, Mark mark) {
        this.webcam = webcam;
        this.mark = mark;
        initializeVision();
    }

    /**
     * Initialize the BoardReader by grabbing an image from webcam.
     */
    public void initializeVision() {
        System.out.println("Please adjust the webcam. When ready, press ENTER.");
        reader.nextLine();
        Mat initIm = webcam.getGrab();
        this.breader = new BoardReader(initIm);
    }

    public GameMove getMove() {
        String user = "";
        while (true) {
            try {
                Thread.sleep(5000);
            } catch(Exception e){
                e.printStackTrace();
            }

            Mat newGrab = webcam.getGrab();

            if (breader.readBoardChanges(newGrab)) {
                GameMove possible = breader.getLastBoardChange();
                System.out.println("Found change: " + possible);
                System.out.println("Was this a move by bot/me?");
                user = reader.nextLine();
                if ( user.equals("bot")) {
                    breader.updateBaseBoard();
                } else if (user.equals("me")) {
                    breader.updateBaseBoard();
                    return new GameMove(mark, possible.getX(), possible.getY());
                } else {
                    System.out.println("Then sleep.");
                }
            } else {
                System.out.println("No changes detected, sleep...");
            }
        }
    }

}
