package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.ArrayList;
import java.util.List;

public class Formater {

    static List<CSVAttribute[]> formatedAtributes = new ArrayList<>();
    static int height;
    static int width;
    static CSVAttribute[][] attributesAsArray;
    static CSVAttribute[][] cache;
    static CSVAttribute[] toMove;

    /**
     *Since the labelIndex can be in different columns, it is moved to the end in order to be able to work with it.
     *
     * @param attributes
     * @param labelIndex
     * @return
     */
    public static List<CSVAttribute[]> format(List<CSVAttribute[]> attributes, int labelIndex) {
        if (attributes.get(0).length == labelIndex - 1)
            return attributes;
        height = attributes.size();
        width = attributes.get(0).length;

        attributesAsArray = new CSVAttribute[height][width];
        cache = new CSVAttribute[height][width];
        toMove = new CSVAttribute[height]; //the label attribute column that needs to be moved
        moveToRightPosition(attributes, labelIndex);
        return formatedAtributes;
    }

    public static void moveToRightPosition(List<CSVAttribute[]> attributes, int labelIndex) {
        for (int k = 0; k < height; k++) {
            attributesAsArray[k] = attributes.get(k); //Converts a list to a 2-dimensional array. Using 2D arrays has been shown to improve performance.
            toMove[k] = attributesAsArray[k][labelIndex];
        }
        for (int k = 0; k < height; k++) {
            int skipper = 0;
            for (int i = 0; i < width - 1; i++) {
                if (i == labelIndex) //the label attribute column is skipped
                    skipper++;
                cache[k][i] = attributesAsArray[k][i + skipper];
            }
            cache[k][width - 1] = toMove[k];
            formatedAtributes.add(cache[k]);
        }
    }
}

