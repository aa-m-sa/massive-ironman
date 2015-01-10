package compvision.lineutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineGroup {
    List<Double> linesRho;
    List<Double> linesTheta;

    public LineGroup() {
        linesRho = new ArrayList<Double>();
        linesTheta = new ArrayList<Double>();
    }
    public LineGroup(double rho, double theta) {
        this();
        addLine(rho, theta);
    }

    public void addLine(double rho, double theta) {
        linesRho.add(rho);
        linesTheta.add(theta);
    }

    public double getLineRho(int index) {
        return linesRho.get(index);
    }
    public double getLineTheta(int index) {
        return linesTheta.get(index);
    }

    public int size() {
        return linesRho.size();
    }

    public double getThetaMean() {
        double sum = 0;
        for (double theta : linesTheta) {
            sum += theta;
        }
        return sum / linesTheta.size();
    }
    public double getRhoMean() {
        double sum = 0;
        for (double rho : linesRho) {
            sum += rho;
        }
        return sum / linesRho.size();
    }


}
