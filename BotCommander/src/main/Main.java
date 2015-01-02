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
                String command = reader.nextLine();
                byte[] outBuffer = new byte[1];
                if (command.equals("quit")) {
                    break;
                } else if (command.equals("ok")) {
                    outBuffer[0] = 0x01;
                    os.write(outBuffer);
                    os.flush();
                } else if (command.equals("draw 0,0")) {
                    outBuffer[0] = 0x10;
                    os.write(outBuffer);
                    os.flush();
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
