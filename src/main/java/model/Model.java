package model;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.special.BesselJ;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.math3.util.FastMath.*;


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

    public LinkedHashMap<Double, Complex> getFunctionList(){
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

    private List<Double> createListOfX(){
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

    private List<Complex> hankelTransform(List<Complex> f, List<Double> rList) {
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
        return result;
    }
}
