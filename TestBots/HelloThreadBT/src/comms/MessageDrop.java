package comms;

/**
 *  Functions as a 'one-place queue', where the next received Message (produced
 *  by BTReader) waits before it's picked up by a Consumer thread that handles
 *  it
 */
public class MessageDrop{

    private boolean empty = true;
    private byte message;

    /**
     * Put a message into a drop.
     */
    public synchronized void put(byte message) {
        while (!empty) {
            // drop already has a message, can't put a new one until the
            // previous is taken away
            try {
                wait();
            } catch (InterruptedException e) {
                // was interrupted
            }
        }

        // Drop is now empty -> put the new message
        empty = false;
        this.message = message;

        // notify that status has changed
        notifyAll();
    }

    public synchronized byte take() {
        while (empty) {
            // drop is empty, nothing to take -> wait
            try {
                wait();
            } catch (InterruptedException e) {
                // was interrupted
            }
        }

        // drop now has a message -> return it
        empty = true;
        notifyAll();
        return message;
    }

}
