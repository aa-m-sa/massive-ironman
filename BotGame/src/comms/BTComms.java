package comms;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTInfo;
import lejos.pc.comm.NXTConnector;


public class BTComms {
    private NXTComm nxtComm;
    private NXTConnector conn;
    private InputStream istream;
    private OutputStream ostream;

    private boolean createBTConnection() {
        try {
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            System.out.println("NXTComm created!");
            NXTInfo[] nxtInfo = nxtComm.search("NXT");
            System.out.println("NXTs found:");
            for (int i = 0; i < nxtInfo.length; i++) {
                System.out.println(nxtInfo[i].toString());
            }
            System.out.println("Choosing the first one...");
            conn = new NXTConnector();
            boolean connected = conn.connectTo(nxtInfo[0], NXTComm.PACKET);
            if (connected) {
                System.out.println("connected");
                return true;
            } else {
                System.out.println(":(");
                return false;
            }
        } catch (NXTCommException e) {
            System.out.println("Connection failed");
            System.out.println(e);
            return false;
        }
    }

    private boolean writeBuffer(byte[] buffer) {
        try {
            ostream.write(buffer);
            ostream.flush();
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }


    /**
     * Connect to Penbot via Bluetooth
     *
     * @return was the connection created succesfully
     */
    public boolean connectToPenbot() {
        if (createBTConnection()) {
            istream = conn.getInputStream();
            ostream = conn.getOutputStream();
            System.out.println("Streams ok!");

            System.out.println("All set!");
            return true;
        }
        return false;
    }

    /**
     * Send a bare command byte
     *
     * @return if succeeded
     */
    public boolean sendCommand(byte commandByte) {
        byte[] outBuffer = new byte[3]; // initially 0
        outBuffer[0] = commandByte;
        return writeBuffer(outBuffer);
    }

    /**
     * Send a command byte and populate rest of the buffer with two ints.
     * In other words, a draw command and coordinates
     * @return if succeeded
     */
    public boolean sendCommand(byte drawByte, int x, int y) {
        byte[] outBuffer = new byte[3];
        outBuffer[0] = drawByte;
        outBuffer[1] = (byte)x;
        outBuffer[2] = (byte)y;
        return writeBuffer(outBuffer);
    }

    public void closeConnections() {

        try {
            nxtComm.close();
        } catch (IOException ioe) {
            System.out.println("IOException closing nxtComm");
            System.out.println(ioe);
        }
    }
}
