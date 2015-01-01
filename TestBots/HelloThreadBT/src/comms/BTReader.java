package comms;

import java.io.*;
import comms.MessageDrop;


/**
 * Implements reading Message bytes from InputStream (over bluetooth) in a
 * separate thread.
 *
 * When has succesfully read a byte, puts it into a Message; a Consumer will
 * read it from there and handle it.
 */

public class BTReader extends Thread {

    private InputStream istream;
    private MessageDrop drop;

    /**
     * Constructor from an open iNputStream to be read.
     */
    public BTReader(InputStream istream, MessageDrop drop) {
        this.istream = istream;
        this.drop = drop;
    }

    public void run() {
        while (true) {
            System.out.println("reading...");
            byte[] buffer = new byte[1];
            try {
                int n = istream.read(buffer);
                if (n > 0) {
                    byte readByte = buffer[0];
                    System.out.println("read: " + readByte);
                    drop.put(readByte);
                } else {
                    System.out.println("End of stream!");
                    return;
                }
            } catch (IOException ioe) {
                System.out.println("IOException");
                //ioe.printStackTrace();
                System.out.println(ioe.getMessage());
                return;
            }
        }

    }

}
