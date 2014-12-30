package comms;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import lejos.nxt.comm.*;

import penbot.Penbot;
import comms.Message;

/**
 * Abstraction for Bluetooth connections
 */
public class BTCommunicator {
    private NXTConnection connection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
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
        this.inputStream = connection.openDataInputStream();
        this.outputStream = connection.openDataOutputStream();
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
        char ch;

        // the first char identifies the type of msg
        ch = this.inputStream.readChar();
        if (ch == 'D') {
            ch = this.inputStream.readChar();
            if (ch == 'X')
                return Message.DRAW_X;
            if (ch == 'O')
                return Message.DRAW_O;
        } else if (ch == 'Q') {
            // a quitting command
            return Message.QUIT;
        }

        // if we're here, received an unknown char in Stream; throw an Exception?
        throw new IOException("Illegal char in inputStr.");

    }

    public int readCoord() throws IOException {
        int c = this.inputStream.readInt();
        if (0 <= c && c <= 2) return c;
        // else incorrect format
        throw new IOException("Illegal int in intpuStr");
    }

    public void stateOk() {
        try {
            this.outputStream.write('A');
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writing 'A' to ostr failed");
        }
    }

    public void stateBusy() {
        try {
            this.outputStream.write('B');
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writin 'B' to ostr failed");
        }
    }

    public void stateError() {
        try {
            this.outputStream.write('E');
            this.outputStream.flush();
        } catch (IOException e) {
            System.out.println("Writin 'E' to ostr failed");
        }
    }

}
