package main;

import java.util.Scanner;

import comms.BTComms;
import comms.Command;

public class ConsoleUI{
    Scanner reader;
    BTComms botConn;

    public ConsoleUI(Scanner reader, BTComms botConn){
        this.reader = reader;
        this.botConn = botConn;
    }

    /**
     * Read user input from Scanner and handle it.
     *
     * @return continue reading input?
     */
    public boolean handleInput() {
        String userInput = reader.nextLine();
        if (userInput.equals("quit")) {
            botConn.sendCommand(Command.QUIT);
            return false;
        } else if (userInput.equals("ok")) {
            botConn.sendCommand(Command.OK);
        } else if (userInput.startsWith("draw")) {
            String[] subs = userInput.split(" ");
            if (subs.length == 3) {
                int x = Integer.parseInt(subs[1]);
                int y = Integer.parseInt(subs[2]);
                if (checkCoord(x) && checkCoord(y)){
                    botConn.sendCommand(Command.DRAW, x, y);
                } else {
                    System.out.println("Not valid draw coordinates!");
                }
            } else {
                System.out.println("Not a valid draw command!");
            }
        } else {
            System.out.println("Unknown input!");
        }
        return true;
    }

    private boolean checkCoord(int x){
        // check if x is a valid board coordinate
        return 0 <= x && x <= 2;
    }
}
