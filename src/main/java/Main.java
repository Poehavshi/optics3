import model.Model;
import org.apache.commons.math3.complex.Complex;
import utility.FunctionsUtility;
import view.PlottingToExcelView;
import view.PlottingToFileView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author Sotnikov Arkadiy (23.05.2022 22:31)
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Model model = new Model();
        PlottingToFileView plottingToFileView = new PlottingToFileView("output");


        LinkedHashMap<Double, Complex> functionValuesMap = model.getFunctionList();
        plottingToFileView.create2DPlotsFromDoubles("Мода Бесселя", functionValuesMap);

        PlottingToExcelView.write("Restored Bessel Amplitude", FunctionsUtility.phase2D(model.restoredFunction(new ArrayList<>(functionValuesMap.values()))));
        PlottingToExcelView.write("Restored Bessel Phase", FunctionsUtility.amplitude2D(model.restoredFunction(new ArrayList<>(functionValuesMap.values()))));

        

    }
}
