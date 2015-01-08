package compvision;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Scanner;

/**
 * Stuff related to reading webcam and displaying its video feed in a window.
 */
public class Webcam implements Runnable {

    private VideoCapture webcam;
    private JFrame window = new JFrame("Webcam");
    private ImageIcon image = new ImageIcon();
    private JLabel label = new JLabel();
    private Mat camGrab = new Mat();

    private volatile boolean running = true;

    public Webcam(int device) {

        System.out.println("Opening device...");
        webcam = new VideoCapture(device);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
            System.out.println("webcam constructor couldn't sleep");
            e.printStackTrace();
        }
        if (!webcam.isOpened()) {
            System.out.println("Webcam not open!");
        }
        System.out.println("Webcam ready!");

        label.setIcon(image);
        window.getContentPane().add(label);
        window.setResizable(false);
        window.setVisible(true);
        updateCam();
    }

    public void run() {
        System.out.println("waiting for cam to open...");

        while (running) {
            updateCam();
            Thread.yield();
        }

        webcam.release();
        window.dispose();
    }

    /**
     * Quit reading webcam feed.
     */
    public void quit() {
        this.running = false;
    }

    private synchronized void updateCam() {
        boolean ok = webcam.read(camGrab);
        image.setImage(toBufferedImage(camGrab));
        window.pack();
        label.updateUI();
    }

    /**
     * Return a copy of current webcam grab.
     * */
    public synchronized Mat getGrab() {
        Mat copyGrab = new Mat();
        camGrab.copyTo(copyGrab);
        return copyGrab;
    }

    /* credit due to https://github.com/master-atul/ImShow-Java-OpenCV */
    public static BufferedImage toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b  = new byte[bufferSize];
        m.get(0,0,b);
        BufferedImage im = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return im;
    }

    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Scanner scanner = new Scanner(System.in);
        Webcam cam = new Webcam(1);
        Thread wt =new Thread(cam);
        wt.start();
        String userIn = "";
        while (!userIn.equals("quit")) {
            userIn = scanner.nextLine();
            Mat test = cam.getGrab();
            Highgui.imwrite("webcam-threaded-test-.jpg", test);
        }
        System.out.println("quitting");
        cam.quit();
        wt.join();
    }
}
