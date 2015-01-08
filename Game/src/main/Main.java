package main;

import java.util.Scanner;

import org.opencv.core.Core;

import game.Game;
import game.Player;
import comms.BTComms;

import game.ui.AssistedWebcamPlayer;
import game.ui.StandardPlayer;

import compvision.Webcam;

public class Main {

    public static void main(String[] args) {
        // must load opencv native libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // webcam
        Webcam webcam = new Webcam(1);
        Thread camThread = new Thread(webcam);
        camThread.start();

        System.out.println("I am a Tic Tac Toe Game");
        System.out.println("Ensure Penbot is calibrated, up and running!");


        // connect to the bot
        BTComms botConn = new BTComms();
        if (!botConn.connectToPenbot()) {
            System.out.println("Connection failed!");
            System.exit(1);
        }

        System.out.println("Connected!");
        // player representation: for now, get player movements form stdin
        // set up stdin input
        Scanner reader = new Scanner(System.in);
        //Player player = new StandardPlayer(reader);
        // TODO determine player movements with OpenCV from webcam
        Player player = new AssistedWebcamPlayer(webcam, reader);

        // create a Game and provide it a player
        Game game = new Game(player, botConn);
        // start the game
        System.out.println("Ready! Press enter to begin...");
        reader.nextLine();
        game.start();

        // quit cleanly
        System.out.print("Closing connections...");
        botConn.closeConnections();
        reader.close();
        System.out.println("OK!");
        System.out.print("Closing webcam...");
        webcam.quit();
        try {
            camThread.join();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException finishing camThread");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.print("webcam closed.");
    }
}
