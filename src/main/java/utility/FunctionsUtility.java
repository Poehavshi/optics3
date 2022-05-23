package utility;

import org.apache.commons.math3.complex.Complex;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sotnikov Arkadiy (23.05.2022 23:45)
 */
public class FunctionsUtility {
    public static List<Double> phase(List<Complex> source) {
        return source.stream().map(Complex::getArgument).collect(Collectors.toList());
    }

    public static List<Double> amplitude(List<Complex> source) {
        return source.stream().map(Complex::abs).collect(Collectors.toList());
    }

    public static List<List<Double>> phase2D(List<List<Complex>> source) {
        return source.stream().map(FunctionsUtility::phase).collect(Collectors.toList());
    }

    public static List<List<Double>> amplitude2D(List<List<Complex>> source) {
        return source.stream().map(FunctionsUtility::amplitude).collect(Collectors.toList());
    }
}
