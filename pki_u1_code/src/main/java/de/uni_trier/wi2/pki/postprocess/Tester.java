package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.List;


public class Tester {
    static int counter;

    /**
     * Returns the value of correctly classified rows from the dataset in %.
     * Returns
     *
     * @param compare
     * @param decisionTree
     * @param labelIndex
     * @return
     */
    public static double test(List<CSVAttribute[]> compare, DecisionTreeNode decisionTree, int labelIndex) {
        counter = 0;
        for (CSVAttribute[] line : compare)
            testLine(line, decisionTree, labelIndex);
        double accuracyOfRightPredicted = (double) counter / (compare.size());
        return accuracyOfRightPredicted * 100;
    }

    /**
     * Checks whether the passed row was correctly predicted from the tree.
     * The column of a row that needs to be checked is taken from the current node via getAttributeIndex().
     * Depending on the value in this column, it then compares the matching child and so on, until the tree has reached labelIndex to check the final result.
     * It is counted how many times the tree is right.
     *
     * @param compare
     * @param decisionTree
     * @param labelIndex
     */
    public static void testLine(CSVAttribute[] compare, DecisionTreeNode decisionTree, int labelIndex) {
        if (decisionTree == null)
            return;
        String toCompare = (String) compare[decisionTree.getAttributeIndex()].getValue();
        if (decisionTree.getAttributeIndex() == labelIndex) {
            if (toCompare.equals(decisionTree.getSplits().keySet().toArray()[0]))
                counter++;
            return;
        }
        DecisionTreeNode child = (DecisionTreeNode) decisionTree.getSplits().get(toCompare);
        testLine(compare, child, labelIndex);
    }
}


