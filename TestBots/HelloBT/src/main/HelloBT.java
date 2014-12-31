package main;

import java.io.*;
import lejos.nxt.comm.*;
import lejos.nxt.Button;

/**
 * Simple bluetooth connection tester bot
 *
 */
public class HelloBT {

	public static void main(String[] args) {
		System.out.println("HELLO, LET'S BT");
		Button.waitForAnyPress();
                System.out.println("Waiting for connection...");
                NXTConnection conn = Bluetooth.waitForConnection();
                System.out.println("Connected!");

                System.out.println("Opening inputstream...");
                InputStream istream = conn.openInputStream();
                System.out.println("OK!");
                System.out.println("Opening outputstream...");
                OutputStream ostream = conn.openOutputStream();
                System.out.println("OK!");

                while (true) {
                    // let's try sending some bytes
                    if (Button.ENTER.isDown()) {
                        System.out.println("HALT");
                        break;
                    }
                    if (Button.LEFT.isDown()) {
                        try {
                            System.out.println("Send 0x11");
                            ostream.write(0x11);
                            ostream.flush();
                            System.out.println(0x11);
                        } catch (IOException ioe) {
                            //
                            System.exit(1);
                        }
                    } else if (Button.RIGHT.isDown()) {
                        try {
                            System.out.println("Send 0x21");
                            ostream.write(0x21);
                            ostream.flush();
                            System.out.println(0x21);
                        } catch (IOException ioe) {
                            //
                            System.exit(1);
                        }
                    } else {
                        try {
                            System.out.println("Send 0xFF");
                            ostream.write(0xFF);
                            ostream.flush();
                            System.out.println(0xFF);
                        } catch (IOException ioe) {
                            //
                            System.exit(1);
                        }
                    }
                    // something was sent, now try to read

                    // read test
                    System.out.println("reading...");
                    byte[] buffer = new byte[1];
                    try {
                        int n = istream.read(buffer);
                        byte readByte = buffer[0];
                        System.out.println("read: " + readByte);
                    } catch (IOException ioe) {
                        System.out.println("IOException while reading");
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException");
                    }
                }

                System.out.println("Done");
                Button.waitForAnyPress();
	}
}
