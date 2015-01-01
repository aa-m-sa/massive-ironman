package main;

import java.io.*;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.*;
import lejos.nxt.Button;

import comms.MessageDrop;
import comms.BTReader;
import comms.MessageHandler;
import comms.LeftListener;


/**
 * Testbot for threaded bluetooth comms:
 *
 * Reading InputStream done in separate thread; when a message is read, it's
 * passed to handler.
 *
 */
public class HelloThreadBT {

	public static void main(String[] args) {

            /* set up button listener for emergency exit*/
            Button.ESCAPE.addButtonListener(new ButtonListener() {
                public void buttonPressed(Button b) {
                    // emergency exit
                    System.exit(1);
                }

                public void buttonReleased(Button b) { }
            });

            /* set up connection */
            System.out.println("Waiting for connection...");
            NXTConnection conn = Bluetooth.waitForConnection();
            System.out.println("Connected!");

            System.out.println("Opening inputstream...");
            InputStream istream = conn.openInputStream();
            System.out.println("OK!");
            System.out.println("Opening outputstream...");
            OutputStream ostream = conn.openOutputStream();
            System.out.println("OK!");

            /* set up button listeners for sending test messages*/
            Button.LEFT.addButtonListener(new LeftListener(ostream));
            /* set up bluetooth message handling*/
            MessageDrop drop = new MessageDrop();
            (new BTReader(istream, drop)).start();
            (new MessageHandler(drop)).start();
            // TODO: do all ^this in e.g. BTCommunications constructor?

            System.out.println("Done");
            Button.waitForAnyPress();
        }
}
