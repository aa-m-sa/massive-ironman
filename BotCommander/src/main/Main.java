package main;

import java.util.Scanner;

import comms.BTComms;

import main.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        System.out.println("I AM BOTCMMDR!");

        Scanner reader = new Scanner(System.in);
        BTComms botConn = new BTComms();
        if (!botConn.connectToPenbot())
            System.exit(1);

        ConsoleUI ui = new ConsoleUI(reader, botConn);
        while (true) {
            System.out.print(">");
            if (!ui.handleInput())
                break;
        }
        System.out.println("Quitting cleanly...");

        botConn.closeConnections();
        reader.close();
    }
}
