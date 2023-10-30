package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.Settings;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.*;

/**
 * Class that holds logic for discretizing values.
 */
public class BinningDiscretizer {

    static double max;
    static double min;
    static int size;
    static boolean binningProcedure = Settings.isBinningProcedure();
    static CSVAttribute[][] attributesAsArray;
    static double intervalSize;
    static int intervalSlot = 1;
    static boolean set = false;
    static ArrayList<CSVAttribute> valuesToDiscretize;

    //For same Frequency
    static int pointCounter, binnumberCounter, pointsPerInterval, overflow;

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     * Counting starts at 1.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public static List<CSVAttribute[]> discretize(int numberOfBins, List<CSVAttribute[]> examples, int attributeId) {
        if (!set) { //this only has to be done once during the runtime
            size = examples.size();
            attributesAsArray = new CSVAttribute[size][];
            attributesAsArray = Main.convertToArray(examples);
            set = true;
        }
        valuesToDiscretize = init(attributeId);
        if (binningProcedure)
            sameFrequency(numberOfBins, examples, attributeId);
        else
            sameQuantityInit(numberOfBins, examples);
        return examples;
    }

    /**
     * Returns the column to be discretized in form of a list.
     * The list is sorted by value.
     *
     * @param attributeId
     * @return
     */
    private static ArrayList<CSVAttribute> init(int attributeId) {
        ArrayList<CSVAttribute> valuesToDiscretize = new ArrayList<>();
        for (int i = 0; i < size; i++)
            valuesToDiscretize.add(attributesAsArray[i][attributeId]);
        sort(valuesToDiscretize);
        return valuesToDiscretize;
    }

    private static void sameFrequency(int numberOfBins, List<CSVAttribute[]> examples, int attributeId) {
        min = (Double) valuesToDiscretize.get(0).getBackUpValue();
        max = (Double) valuesToDiscretize.get(valuesToDiscretize.size() - 1).getBackUpValue();
        intervalSize = (max - min) / numberOfBins;
        for (int i = 0; i < examples.size(); i++) {
            while ((double) attributesAsArray[i][attributeId].getBackUpValue() - min > intervalSize * intervalSlot)    // As long as intervalsize*intervalSlot is smaller than the attribute value, the intervalSlot is increased by 1. Since the bins should start with one, we subtract the smallest known value of the column.
                intervalSlot++;
            attributesAsArray[i][attributeId].setValue(Integer.toString(intervalSlot));       //Zuweisung des intervalSlot als String
            intervalSlot = 1;
        }
    }

    /**
     * In case of a dividable bin number, the first bins contain one element more.
     *
     * @param numberOfBins
     * @param examples
     */
    private static void sameQuantityInit(int numberOfBins, List<CSVAttribute[]> examples) {
        //Wenn sich die Elemente nicht sauber auf die Anzahl der Bins verteilen lassen, werden vom kleinsten Intervall ausgehend jedem Intervall ein Element mehr zugeteilt
        if (numberOfBins > examples.size()) {     //Es wird einmalig eine Fehlermeldung ausgegeben
            System.out.println("More bins than data points were selected, I proceed with a number of bins equal to data points quantity.");
            numberOfBins = examples.size();
        }
        pointsPerInterval = examples.size() / numberOfBins;
        overflow = examples.size() % numberOfBins;
        binnumberCounter = 1;
        pointCounter = 0;   //Counts the points per interval (should not get larger than pointsPerInterval)
        if (overflow > 0) {
            System.out.println("Due to uneven dividable bin number, the first " + overflow + " intervals contain one element more.");
            pointCounter = -1;
            overflow--;
        }
        sameQuantity(examples);
    }

    /**
     *
     * Since the list is sorted, its values are simply iterated over.
     * and receive a bin number. The number gets increased when the number of data points per interval is reached.
     * @param examples
     */
    private static void sameQuantity(List<CSVAttribute[]> examples) {
        for (int k = 0; k < examples.size(); k++) {
            valuesToDiscretize.get(k).setValue(Integer.toString(binnumberCounter));
            pointCounter++;
            if (pointCounter == pointsPerInterval && overflow == 0) {
                pointCounter = 0;
                binnumberCounter++;
            }
            if (pointCounter == pointsPerInterval && overflow > 0) {
                pointCounter = -1;       // One element more should be distributed per interval until the rest has been depleted
                binnumberCounter++;
                overflow--;     // The overflow is depleted
            }
        }
    }

    private static void sort(ArrayList<CSVAttribute> valuesToDiscretize) {
        valuesToDiscretize.sort(Comparator.comparing(attribute -> ((Double) attribute.getBackUpValue())));
    }
}

