package compvision;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Stuff related to reading webcam and displaying its video feed in a window.
 */
public class Webcam implements Runnable {

    private VideoCapture webcam;
    private JFrame window = new JFrame("Webcam");
    private ImageIcon image = new ImageIcon();
    private JLabel label = new JLabel();

    public Webcam(int device) throws InterruptedException {

        System.out.println("Opening device...");
        webcam = new VideoCapture(device);
        Thread.sleep(1000);
        if (!webcam.isOpened()) {
            System.out.println("Webcam not open!");
        }
        System.out.println("Webcam ready!");

        label.setIcon(image);
        window.getContentPane().add(label);
        window.setResizable(false);
    }

    public void run() {
        Mat camGrab = new Mat();
        System.out.println("waiting for cam to open...");

        for (int i = 0; ; i++) {
            boolean ok = webcam.read(camGrab);
            image.setImage(toBufferedImage(camGrab));
            window.pack();
            label.updateUI();
            window.setVisible(true);
        }
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
        (new Thread(new Webcam(1))).start();
    }
}
