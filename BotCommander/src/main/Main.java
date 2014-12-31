package main;

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


public class Main {


    public static byte readByte(InputStream inStream) throws IOException {
        // reads a byte from stream
        byte[] buffer = new byte[1];
        int n = 0;
        while (n < 1) {
            n = inStream.read(buffer);
        }
        return buffer[0];
    }

    public static void main(String[] args) throws NXTCommException {

        System.out.println("I AM BOTCMMDR!");

        Scanner reader = new Scanner(System.in);
        NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
        System.out.println("NXTComm created!");
        try {

            NXTInfo[] nxtInfo = nxtComm.search("NXT");
            System.out.println("NXTs found:");
            for (int i = 0; i < nxtInfo.length; i++) {
                System.out.println(nxtInfo[i].toString());
            }
            System.out.println("Choosing the first one...");
            NXTConnector conn = new NXTConnector();
            boolean connected = conn.connectTo(nxtInfo[0], NXTComm.PACKET);
            if (connected) {
                System.out.println("connected");
            } else {
                System.out.println(":(");
                System.exit(1);
            }


            InputStream is = conn.getInputStream();
            OutputStream os = conn.getOutputStream();
            System.out.println("Streams ok!");

            System.out.println("All set!");

            while (true) {
                // Penbot will write only chars to DataOutput (our Input)
            	System.out.println("reading is...");
                byte read = readByte(is);
                System.out.println("read!");
                System.out.println(read);

                System.out.print(">");
                String input = reader.nextLine();
                if (input.startsWith("draw ")) {
                    // exact syntax: "draw x,y", x,y ints
                    int x = Character.getNumericValue(input.charAt(5));
                    int y = Character.getNumericValue(input.charAt(7));
                    System.out.println("Sending a draw command: ");
                    System.out.println(x);
                    System.out.println(y);
                    os.write((byte) 0x12);
                    os.write((byte) x);
                    os.write((byte) y);
                } else if (input.startsWith("quit")) {
                    os.write((byte) 0x11);
                    System.out.println(0x11);
                    break;
                } else if (input.startsWith("ok")) {
                    System.out.println(0x10);
                    os.write((byte) 0x10);
                }
            }

            System.out.println("Quitting cleanly...");

        } catch (NXTCommException e) {
            System.out.println("Connection failed");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("IO failed");
            System.out.println(e);
        } finally {
            reader.close();
            if (nxtComm != null) {
                try {
                    nxtComm.close();
                } catch (IOException ioe) {
                    System.out.println("IOException closing nxtComm");
                    System.out.println(ioe);
                }
            }
        }
    }
}
