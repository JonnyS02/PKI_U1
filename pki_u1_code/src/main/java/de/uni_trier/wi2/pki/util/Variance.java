package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Variance {

    /**
     * Only works with continuous values.
     * @param matrix
     * @param labelIndex
     * @return
     */

    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
        List<Double> outcome = new ArrayList<>();
        CSVAttribute[][] attributesAsArray = Main.convertToArray((List<CSVAttribute[]>) matrix);
        for (int i = 0; i < attributesAsArray[0].length; i++)
            outcome.add(calcEntropy(attributesAsArray, labelIndex));
        return outcome;
    }

    private static double calcMean(CSVAttribute[][] attributesAsArray, int labelIndex) {
        double value = 0;
        for (int i = 0; i < attributesAsArray.length; i++)
            value = Double.sum(value, (double) attributesAsArray[i][labelIndex].getBackUpValue());
        return value/attributesAsArray.length;
    }

    private static double calcEntropy(CSVAttribute[][] attributesAsArray, int labelIndex) {
        double mean = - calcMean(attributesAsArray, labelIndex);
        double value ;
        double Gain = 0;
        for (int i = 0; i < attributesAsArray.length; i++) {
           value = Double.sum((Double)attributesAsArray[i][labelIndex].getBackUpValue(),mean);
           value = value*value;
           Gain = Double.sum(Gain,value/attributesAsArray.length);
        }
        return Gain;
    }

}
