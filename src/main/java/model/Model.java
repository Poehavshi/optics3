package model;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.special.BesselJ;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.math3.util.FastMath.*;
import static utility.FunctionsUtility.*;


/**
 * @author Sotnikov Arkadiy (23.05.2022 22:31)
 */

public class Model {
    private static final Logger log = Logger.getLogger(Model.class.getName());

    private static final double R = 5;
    private static final int SIZE_OF_LIST = 1024;
    private static final double m = -5;
    private static final int n = 200;

    private static final double alpha = 1;
    private static final double p = -5;
    private static final BesselJ besselJ = new BesselJ(abs(p));
    private static final BesselJ besselJForHankel = new BesselJ(abs(m));

    private static final double h = R / n;

    private Complex f(double r) {
        return Complex.I.multiply(p).exp().multiply(besselJ.value(alpha * r));
    }

    public LinkedHashMap<Double, Complex> createFunctionList(){
        List<Double> xs = createListOfX();

        List<Complex> ys = xs.stream()
                .map(this::f)
                .collect(Collectors.toList());

        LinkedHashMap<Double, Complex> result = new LinkedHashMap<>();
        for (int i = 0; i<SIZE_OF_LIST; ++i){
            result.put(xs.get(i), ys.get(i));
        }
        return result;
    }

    public List<Double> createListOfX(){
        List<Double> values = new ArrayList<>();
        double step = R / SIZE_OF_LIST;
        for (double x = 0; x <= R; x += step) { values.add(x); }
        return values;
    }

    public List<List<Complex>> restoredFunction(List<Complex> functionValuesList) {
        List<List<Complex>> restoredFunction = new ArrayList<>();

        int a;
        for (int i = 0; i < functionValuesList.size() * 2 + 1; ++i) {
            List<Complex> row = new ArrayList<>();
            for (int j = 0; j < functionValuesList.size() * 2 + 1; ++j) {
                a = (int) round(sqrt(pow(i - n, 2) + pow(j - n, 2)));

                if (a >= n)
                    row.add(Complex.ZERO);
                else
                    row.add(functionValuesList.get(a)
                            .multiply(
                                    Complex.I.multiply(m * atan2(j - n, i - n))
                                            .exp()
                            ));
            }
            restoredFunction.add(row);
        }

        return restoredFunction;
    }

    public LinkedHashMap<Double, Complex> createHankelTransformList() {
        List<Double> rList = this.createListOfX();
        List<Complex> f = rList.stream().map(this::f).collect(Collectors.toList());

        double hankelTime = System.nanoTime();

        List<Complex> result = new ArrayList<>();
        for (int i = 0; i < rList.size(); i++) {
            List<Double> besselValues = new ArrayList<>();
            for (Double r : rList)
                besselValues.add(besselJForHankel.value(2 * Math.PI * rList.get(i) * r));

            Complex ro = IntStream.range(0, besselValues.size())
                    .mapToObj(k -> f.get(k)
                            .multiply(besselValues.get(k))
                            .multiply(rList.get(k))
                            .multiply(h))
                    .reduce(Complex::add)
                    .get();

            result.add(ro);
        }

        result = result.stream()
                .map(e -> e.multiply(Complex.valueOf(2).multiply(Math.PI).divide(Complex.I.pow(m))))
                .collect(Collectors.toList());

        hankelTime = (System.nanoTime() - hankelTime) / 1_000_000;
        log.info("Hankel transformation time: " + hankelTime + " ms");

        LinkedHashMap<Double, Complex> mapResult = new LinkedHashMap<>();
        for (int i = 0; i<SIZE_OF_LIST; ++i){
            mapResult.put(rList.get(i), result.get(i));
        }

        return mapResult;
    }

    private List<Complex> fastFourierTransform(List<Complex> toTransform) {
        return Arrays.asList(
                new FastFourierTransformer(DftNormalization.STANDARD)
                        .transform(toTransform.toArray(new Complex[0]), TransformType.FORWARD)
        );
    }

    private List<List<Complex>> fastFourierTransform2(List<List<Complex>> toTransform) {
        List<List<Complex>> result = new ArrayList<>();
        for (List<Complex> row : toTransform) {
            result.add(fastFourierTransform(row));
        }

        result = transpose(result);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, fastFourierTransform(result.get(i)));
        }
        return transpose(result);
    }

    public List<List<Complex>> createFFT2DList() {
        List<Double> xs = createListOfX();

        List<Complex> functionValues = xs.stream()
                .map(this::f)
                .collect(Collectors.toList());
        List<List<Complex>> toTransform = restoredFunction(functionValues);

        double fftTime = System.nanoTime();

        List<List<Complex>> copyOfFunction = new ArrayList<>(toTransform);
        addZerosToListSize2(copyOfFunction, SIZE_OF_LIST*4);

        copyOfFunction = swapList2(copyOfFunction);
        List<List<Complex>> transformedList = fastFourierTransform2(copyOfFunction);
        transformedList = multiply2(transformedList, h);
        transformedList = swapList2(transformedList);
        transformedList = getElementsFromCenter2(transformedList, n * 2 + 1);

        fftTime = (System.nanoTime() - fftTime) / 1_000_000;
        log.info("FFT transformation time: " + fftTime + " ms");

        return transformedList;
    }

}
