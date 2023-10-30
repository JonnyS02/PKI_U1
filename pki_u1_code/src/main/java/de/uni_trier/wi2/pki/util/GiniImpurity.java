package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GiniImpurity {

    static List<String> range;
    static CSVAttribute[][] attributesAsArray;
    static int sizeOfMatrix;

    /**
     * Calculates the information gain for all attributes. Works essentially like EntropyUtils with just some minor adjustments to the formula.
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>.
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing.
     *                   football, than labelIndex is 2.
     * @return the information gain for each attribute.
     */
    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
        List<Double> gains = new ArrayList<>();     //A list of gain values
        List<CSVAttribute[]> listMatrix = (List<CSVAttribute[]>) matrix;   //Conversion from Collection to List
        range = Main.rangeFinder(listMatrix, labelIndex);
        if (range.size() <= 1)     //If there is only one value left in the range, a leaf node has been reached
            return (null);
        sizeOfMatrix = matrix.size();
        attributesAsArray = Main.convertToArray(listMatrix);
        for (int i = 0; i < listMatrix.get(0).length; i++) {//Iterates over all columns (except for labelIndex) and determines their information content
            if (!(i == labelIndex)) {
                gains.add(  ra(listMatrix, i, labelIndex));  //Gain(A) = H(E) - R(A)
            }
        }
        return gains;
    }

    /**
     * Calculates R(A) from the labelIndexToAnalyze column.
     * @param matrix
     * @param labelIndexToAnalyze
     * @param labelIndex
     * @return
     */
    private static double ra(List<CSVAttribute[]> matrix, int labelIndexToAnalyze, int labelIndex) {
        double ra = 0;
        List<String> rangeToAnalyze = Main.rangeFinder(matrix, labelIndexToAnalyze);
        int[] rangeCounter = Main.rangeCounter(matrix, rangeToAnalyze, labelIndexToAnalyze);
        for (int k = 0; k < rangeToAnalyze.size(); k++) { //All different attribute values of the labelIndexToAnalyze column are iterated over
            double heOfk = 0;
            heOfk = calculateOccurrencesLabelIndex(labelIndexToAnalyze, rangeToAnalyze.get(k), labelIndex, rangeCounter[k]);
            ra = ra + ((double) rangeCounter[k] / matrix.size()) * heOfk;
        }
        return ra;
    }

    /**
     * Iterates over all the different attribute values of the labelIndex column (the column whose value is to be predicted).
     *
     * @param labelIndexToAnalyze
     * @param rangeToAnalyze
     * @param labelIndex
     * @param amount
     * @return
     */
    private static double calculateOccurrencesLabelIndex(int labelIndexToAnalyze, String rangeToAnalyze, int labelIndex, int amount) {
        double heOfk = 1;
        for (int i = 0; i < range.size(); i++) {
            double counter = countOcourences(labelIndexToAnalyze, rangeToAnalyze, labelIndex, range.get(i));
            if (counter != 0) { //H(E) or hek is calculated for the characteristic k and added to the total R(A) or ra
                double a =  counter / amount;
                heOfk = heOfk - a * a;      //H(E) or hek for the value k is calculated by adding up
            }
        }
        return heOfk;
    }

    /**
     * The entire table is iterated line by line, and it is tested whether the attribute value from labelIndexToAnalyze and the attribute value from labelIndex occur at the same time.
     *
     * @param labelIndexToAnalyze
     * @param valueToAnalyze
     * @param labelIndex
     * @param vlaueFromRange
     * @return
     */
    public static int countOcourences(int labelIndexToAnalyze, String valueToAnalyze, int labelIndex, String vlaueFromRange) {
        int counter = 0;
        for (int j = 0; j < sizeOfMatrix; j++) {
            if (attributesAsArray[j][labelIndexToAnalyze].getValue().equals(valueToAnalyze) && attributesAsArray[j][labelIndex].getValue().equals(vlaueFromRange)) {
                counter++;
            }
        }
        return counter;
    }


}
