package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.Settings;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {

    static int formularForGain = Settings.getFormularForGain();
    static List<Double> outcome = new ArrayList<>();

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree.
     */

    public static DecisionTreeNode createTree(Collection<CSVAttribute[]> examples, int labelIndex) {
        List<CSVAttribute[]> attributes = (List<CSVAttribute[]>) examples;

        if (Main.rangeFinder(attributes, labelIndex).size() <= 1) // If only one unique value occurs at the labelIndex position, a leaf node is created with this value
            return setLeaf(attributes, labelIndex);
        int bestIndex = getIndexOfBestAttribute(examples, labelIndex);

        if (outcome == null || outcome.size() == 0 || outcome.get(bestIndex) == 0) // When a point is reached where the information gain for each column is zero, a leaf node with the most common label index is inserted.
            return gainIsZero(attributes, labelIndex);

        CSVAttribute[][] attributesAsArray = Main.convertToArray(attributes);
        return setupRoot(bestIndex, attributesAsArray, labelIndex, attributes);
    }

    /**
     * After identifying the best column for creating the branches, a Child node is added for each different value of this column.
     *
     * @param bestIndex
     * @param attributesAsArray
     * @param labelIndex
     * @param attributes
     * @return
     */
    private static DecisionTreeNode setupRoot(int bestIndex, CSVAttribute[][] attributesAsArray, int labelIndex, List<CSVAttribute[]> attributes) {
        DecisionTreeNode root = new DecisionTreeNode();
        root.setAttributeIndex((int) attributesAsArray[0][bestIndex].getAttributIndex());
        List<String> range = Main.rangeFinder(attributes, bestIndex);
        for (int k = 0; k < range.size(); k++) {
            DecisionTreeNode child = createTree(calcSublist(bestIndex, range.get(k), attributesAsArray), labelIndex - 1); //labelIndex needs to be decreased since the sublist lost one column
            child.setParent(root);
            root.setSplits(range.get(k), child);
        }
        return root;
    }

    /**
     * A new sublist is now created that no longer contains the attribute that was previously compared. However, the list only contains lines in which the value of the compared column was the same.
     * This means there are as many sub-lists as there were different values for the column.
     *
     * @param bestIndex
     * @param rangeValue
     * @param attributesAsArray
     * @return
     */
    private static LinkedList<CSVAttribute[]> calcSublist(int bestIndex, String rangeValue, CSVAttribute[][] attributesAsArray) {
        LinkedList<CSVAttribute[]> attributesAfterSplit = new LinkedList<CSVAttribute[]>();
        for (int i = 0; i < attributesAsArray.length; i++) {
            if (attributesAsArray[i][bestIndex].getValue().equals(rangeValue)) {
                LinkedList<CSVAttribute> test = new LinkedList<>();
                for (int j = 0; j < attributesAsArray[i].length; j++) {
                    if (!(j == bestIndex)) {
                        test.add(attributesAsArray[i][j]);
                    }
                }
                attributesAfterSplit.add(test.toArray(new CSVAttribute[0]));
            }
        }
        return attributesAfterSplit;
    }


    private static DecisionTreeNode setLeaf(List<CSVAttribute[]> attributes, int labelIndex) {
        DecisionTreeNode root = new DecisionTreeNode();
        root.setAttributeIndex((int) attributes.get(0)[labelIndex].getAttributIndex());
        root.setSplits((String) attributes.get(0)[labelIndex].getValue(), null);        //The leaf node gets the value of labelIndex
        return root;
    }

    /**
     * Find the best column to compare.
     *
     * @param examples
     * @param labelIndex
     * @return
     */
    private static int getIndexOfBestAttribute(Collection<CSVAttribute[]> examples, int labelIndex) {
        int bestIndex = 0;
        setForlmularForIGain(examples, labelIndex);
        for (int k = 0; k < outcome.size(); k++) {      //Ermittlung des größten Informationsgehalts aus den aktuellen Spalten
            if (outcome.get(k) > outcome.get(bestIndex)) {
                bestIndex = k;
            }
        }
        return bestIndex;
    }

    /**
     * Sets the formula for calculating the information gain and generates a list with information gain for every colum.
     * @param examples
     * @param labelIndex
     */
    private static void setForlmularForIGain(Collection<CSVAttribute[]> examples, int labelIndex) {
        switch (formularForGain) {
            case 1:
                outcome = EntropyUtils.calcInformationGain(examples, labelIndex);
                break;
            case 2:
                outcome = Variance.calcInformationGain(examples, labelIndex);
                break;
            case 3:
                outcome = GiniImpurity.calcInformationGain(examples, labelIndex);
                break;
        }
    }

    /**
     *In order to still be able to assign a leaf node, it is determined which labelIndex value occurs most frequently, this is selected as the LeafNode class.
     * @param attributes
     * @param labelIndex
     * @return
     */
    private static DecisionTreeNode gainIsZero(List<CSVAttribute[]> attributes, int labelIndex) {
        DecisionTreeNode root = new DecisionTreeNode();
        List range = Main.rangeFinder(attributes, labelIndex);
        int[] rangeCounter = Main.rangeCounter(attributes, range, labelIndex);
        int mostLikelyValue = 0;
        for (int k = 0; k < rangeCounter.length; k++) {
            if (rangeCounter[mostLikelyValue] < rangeCounter[k])
                mostLikelyValue = k;
        }
        root.setSplits("" + range.get(mostLikelyValue), null);
        root.setAttributeIndex((int) attributes.get(0)[labelIndex].getAttributIndex());
        return root;
    }
}
