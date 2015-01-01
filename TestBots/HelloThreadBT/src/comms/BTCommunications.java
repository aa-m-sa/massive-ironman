package comms;

import lejos.nxt.comm.*;
import java.io.*;
import comms.BTReader;

/**
 * Provides abstraction for bluetooth communications
 */

public class BTCommunications {
    NXTConnection conn;
    InputStream istream;
    OutputStream ostream;

    public BTCommunications() {
        System.out.println("Waiting for connection...");
        conn = Bluetooth.waitForConnection();
        System.out.println("Connected!");

        System.out.println("Opening inputstream...");
        istream = conn.openInputStream();
        System.out.println("OK!");
        System.out.println("Opening outputstream...");
        ostream = conn.openOutputStream();
        System.out.println("OK!");
    }

}
