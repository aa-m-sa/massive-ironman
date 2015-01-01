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




    public static void main(String[] args) throws NXTCommException, InterruptedException {

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

            System.out.println(is.markSupported());

            while (true) {
                // user io
                //System.out.print(">");
                //String user = reader.nextLine();

                // Receive
                byte readByte;
                System.out.println("reading is...");
                byte[] buffer = new byte[1];
                int n = is.read(buffer);
                readByte = buffer[0];
                System.out.println(n);
                if (n > 0) {
                    System.out.print("read! : ");
                    System.out.println(n + ", " + readByte);

                    // read successfully -> send
                    byte sendByte = 0x33;
                    System.out.print("Writing 0x33...");
                    os.write(sendByte);
                    os.flush();
                    System.out.println("...wrote " + 0x33);
                } else {
                    System.exit(1);
                }

                Thread.sleep(1000);

            }

            //System.out.println("Quitting cleanly...");

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
