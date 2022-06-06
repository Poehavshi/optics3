package utility;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public static List<Complex> addZerosToListToSize(List<Complex> list, int size) {
        int zeroCount = size - list.size();

        for (int i = 0; i < zeroCount; i += 2) {
            list.add(Complex.ZERO);
            list.add(0, Complex.ZERO);
        }

        if (zeroCount % 2 != 0) {
            list.remove(0);
        }

        return list;
    }

    public static <T> List<T> swapList(List<T> list) {
        int listMiddle = list.size() / 2;
        List<T> resultList = new ArrayList<>(list.subList(listMiddle, list.size()));
        resultList.addAll(list.subList(0, listMiddle));
        return resultList;
    }

    public static List<Complex> multiply(List<Complex> source, double multiplier) {
        return source.stream().map(e -> e.multiply(multiplier)).collect(Collectors.toList());
    }

    public static <T> List<T> getElementsFromCenter(List<T> list, int size) {
        int center = list.size() / 2;
        return list.subList(center - (size / 2), center + (size / 2));
    }

    public static List<List<Complex>> swapList2(List<List<Complex>> list) {
        List<List<Complex>> result = new ArrayList<>();
        for (List<Complex> row : swapList(list)) {
            result.add(swapList(row));
        }
        return result;
    }

    public static List<List<Complex>> multiply2(List<List<Complex>> source, double multiplier) {
        var result = new ArrayList<List<Complex>>();
        for (List<Complex> row : source) {
            result.add(multiply(row, multiplier));
        }
        return result;
    }

    public static <T> List<List<T>> getElementsFromCenter2(List<List<T>> list, int size) {
        var elementsFromCenter = getElementsFromCenter(list, size);
        var result = new ArrayList<List<T>>();
        for (List<T> row : elementsFromCenter) {
            result.add(getElementsFromCenter(row, size));
        }
        return result;
    }

    public static void addZerosToListSize2(List<List<Complex>> list, int needSize) {
        int zerosSize = needSize - list.size();

        for (List<Complex> row : list) {
            addZerosToListToSize(row, needSize);
        }

        List<Complex> zeros = Collections.nCopies(list.size() + zerosSize, Complex.ZERO);
        for (int i = 0; i < zerosSize; i += 2) {
            list.add(new ArrayList<>(zeros));
            list.add(0, new ArrayList<>(zeros));
        }

        if (zerosSize % 2 != 0) {
            list.remove(0);
        }
    }

    public static List<List<Complex>> transpose(List<List<Complex>> matrix) {
        Complex[][] array = matrix.stream().map(e -> e.toArray(new Complex[0])).toArray(Complex[][]::new);
        Complex[][] transposedArray = MatrixUtils.createFieldMatrix(array).transpose().getData();
        return Arrays.stream(transposedArray)
                .map(Arrays::asList)
                .collect(Collectors.toList());
    }
}
