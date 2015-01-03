package main;

import game.Game;

import java.util.Scanner;

import comms.BTComms;

import main.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        System.out.println("I am a Tic Tac Toe Game");
        System.out.println("Ensure Penbot is calibrated, up and running!");

        // set up stdin input
        Scanner reader = new Scanner(System.in);

        // connect to the bot
        BTComms botConn = new BTComms();
        if (!botConn.connectToPenbot()) {
            System.out.println("Connection failed!");
            System.exit(1);
        }
        // player representation: for now, get player movements form stdin
        Player player = new ConsolePlayer(reader);
        // TODO determine player movements with OpenCV from webcam
        // Player player = new WebcamPlayer(webcam) or something;
        Game game = new Game(player, botConn);
        // start the game
        game.start();

        /**
        System.out.println("I AM BOTCMMDR!");


        ConsoleUI ui = new ConsoleUI(reader, botConn);
        while (true) {
            System.out.print(">");
            if (!ui.handleInput())
                break;
        }
        System.out.println("Quitting cleanly...");

        **/

        // quit cleanly
        botConn.closeConnections();
        reader.close();
    }
}
