package view;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sotnikov Arkadiy (23.05.2022 22:47)
 */

public class PlottingToFileView {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    private final String pathToDirectory;

    public PlottingToFileView(String outputDirectory) {
        File directory = new File(outputDirectory);
        if (!directory.exists()) {
            boolean success = directory.mkdir();
        }
        pathToDirectory = outputDirectory;
    }

    private XYDataset createAmplitudeDataset(String title, LinkedHashMap<Double, Complex> coords) {
        XYSeries xySeries = new XYSeries(title);
        for (Map.Entry<Double, Complex> entry : coords.entrySet()) {
            Double x = entry.getKey();
            Double y = entry.getValue().abs();
            xySeries.add(x, y);
        }
        return new XYSeriesCollection(xySeries);
    }

    private XYDataset createPhaseDataset(String title, LinkedHashMap<Double, Complex> coords) {
        XYSeries xySeries = new XYSeries(title);
        for (Map.Entry<Double, Complex> entry : coords.entrySet()) {
            Double x = entry.getKey();
            Double y = entry.getValue().getArgument();
            xySeries.add(x, y);
        }
        return new XYSeriesCollection(xySeries);
    }


    /**
     * Create .jpeg image of plot
     *
     * @param title title of file
     * @param coords     X and Y axis
     */
    public void create2DPlotsFromDoubles(String title, LinkedHashMap<Double, Complex> coords) {
        XYDataset amplitudeDataset = createAmplitudeDataset(title, coords);

        JFreeChart amplitudeChart = ChartFactory.createXYLineChart(
                title,
                "r",
                "Amplitude",
                amplitudeDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        try {
            ChartUtilities.saveChartAsJPEG(
                    new File(pathToDirectory + "/" + title + " amplitude.jpeg"),
                    amplitudeChart,
                    WIDTH,
                    HEIGHT
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        XYDataset phaseDataset = createPhaseDataset(title, coords);

        JFreeChart phaseChart = ChartFactory.createXYLineChart(
                title,
                "r",
                "phase",
                phaseDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        try {
            ChartUtilities.saveChartAsJPEG(
                    new File(pathToDirectory + "/" + title + " phase.jpeg"),
                    phaseChart,
                    WIDTH,
                    HEIGHT
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
