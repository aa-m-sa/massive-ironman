package comms;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import lejos.nxt.comm.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;

import penbot.Penbot;

import comms.Message;

/**
 * Abstraction for Bluetooth connections
 */
public class BTCommunicator {
    private NXTConnection connection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Penbot bot;

    public BTCommunicator() {

        System.out.println("Initializing connection...");
        this.connection = Bluetooth.waitForConnection();
        System.out.println("Connected!");

    }

    /**
     * Open the input and output streams
     */
    public void open() {
        System.out.println("Opening streams...");
        this.inputStream = connection.openInputStream();
        this.outputStream = connection.openOutputStream();
        System.out.println("Streams opened!");
    }

    /**
     * Close the input and output streams cleanly
     */
    public void close() {
        try {
            this.inputStream.close();
        } catch (IOException e) {
            System.out.println("IOException closing inputStr\n continuing...");
        }

        try {
            this.outputStream.close();
        } catch (IOException e) {
            System.out.println("IOException closing outputStr\n continuing...");
        }
    }

    /**
     *  Reads a new message from the input command stream and returns a corresponding
     *  Command.  */
    public Message readMessage() throws IOException {
        byte[] buffer = new byte[1];
        // the first char identifies the type of msg
        int numRead = 0;
        LCD.clear();
        LCD.drawChar('n', 1, 1);
        try {
            numRead = this.inputStream.read(buffer);
        } catch (IOException ioe) {
            LCD.refresh();
            LCD.drawChar('e', 1,2);
            LCD.refresh();
            return Message.OK;
        }

        LCD.refresh();
        LCD.drawInt(numRead, 1, 2);
        LCD.drawChar('#', 2, 2);
        LCD.drawInt((int)buffer[0], 3, 2);
        LCD.drawChar('#', 4, 2);
        LCD.refresh();
        Button.waitForAnyPress();
        LCD.refresh();
        if (buffer[0] == 0x00) {
            // 0 test, with OK
            return Message.OK;
        }
        if (buffer[0] ==0x10) {
            // 0x10 = OK
            // empty, ok msg
            System.out.println("Received OK");
            return Message.OK;
        }
        if (buffer[0] == 0x11) {
            // 0x11 = QUIT
            System.out.println("Received QUIT");
            // a quitting command
            return Message.QUIT;
        }
        if (buffer[0] == 0x12){
            // 0x12 = DRAW_X
            // next 2 bytes will specify x, y coordinates
            // reading done by readCoord
            System.out.println("Received DRAW_X!");
            return Message.DRAW_X;
        }
        // if we're here, received an unknown char in Stream; throw an Exception?
        //throw new IOException("Illegal char in inputStr:\n " + buffer[0]);
        System.out.println("ARGSASDAD");
        LCD.refresh();
        return Message.OK;

    }

    public int readCoord() throws IOException {
        byte[] cBuffer = new byte[1];
        int cNum = 0;
        while (cNum < 1) {
            cNum = inputStream.read(cBuffer);
        }
        return (int) cBuffer[0];
    }

    public void stateOk() {
        try {
            this.outputStream.write(0x20);
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writing OK 0x20 to ostr failed");
        }
    }

    public void stateBusy() {
        try {
            this.outputStream.write(0x21);
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writin BUSY 0x21 to ostr failed");
        }
    }

    public void stateError() {
        try {
            this.outputStream.write(0xFF);
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writin ERROR 0xFF to ostr failed");
        }
    }

}
