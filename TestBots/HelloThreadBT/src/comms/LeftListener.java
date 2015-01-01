package comms;

import lejos.nxt.ButtonListener;
import lejos.nxt.Button;

import java.io.*;

public class LeftListener implements ButtonListener {
    private OutputStream ostream;

    public LeftListener(OutputStream ostream) {
        this.ostream = ostream;
    }

    public void buttonPressed(Button b) {
        System.out.println("Sending 0x11...");
        try {
            ostream.write(0x11);
            ostream.flush();
            System.out.println("Sent " + 0x11);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void buttonReleased(Button b) { }
}
