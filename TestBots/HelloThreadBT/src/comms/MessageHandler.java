package comms;

import comms.MessageDrop;

/**
 * A Consumer that handles messages produced by BTReader
 */

public class MessageHandler extends Thread {
    private MessageDrop drop;

    public MessageHandler(MessageDrop drop) {
        this.drop = drop;
    }

    public void run() {
        while (true) {
            byte message = drop.take();
            // the handling happens here
            System.out.println("Received message " + message);
        }
    }

}
