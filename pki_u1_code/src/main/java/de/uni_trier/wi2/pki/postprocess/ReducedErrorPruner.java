package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;
import java.util.List;

/**
 * Prunes a trained decision tree in a post-pruning way.
 */
public class ReducedErrorPruner {

    /**
     * Prunes the given decision tree in-place.
     *
     * @param trainedDecisionTree The decision tree to prune.
     * @param validationExamples  the examples to validate the pruning with.
     * @param labelAttributeId    The label attribute.
     */
    int labelAttributeId;
    Collection<CSVAttribute[]> validationExamples;
    DecisionTreeNode trainedDecisionTree;

    double originalClassificationAccuracy;
    List range;
    HashMap<String, DecisionTreeNode> splitsCache;
    int attributeIndexCache;
    DecisionTreeNode nodeToPrune;
    double currentBest = 0;
    double toCompare = 0;

    /**
     * Prunes the given root as long as originalClassificationAccuracyClassificationAccuracy gets better.
     *
     * @param trainedDecisionTree
     * @param validationExamples
     * @param labelAttributeId
     */
    public void prune(DecisionTreeNode trainedDecisionTree, Collection<CSVAttribute[]> validationExamples, int labelAttributeId) {
        this.trainedDecisionTree = trainedDecisionTree;
        this.validationExamples = validationExamples;
        this.labelAttributeId = labelAttributeId;
        range = Main.rangeFinder((List) validationExamples, labelAttributeId);
        originalClassificationAccuracy = Tester.test((List<CSVAttribute[]>) validationExamples, trainedDecisionTree, labelAttributeId);
        setProminentLabel(trainedDecisionTree);
        do {
            currentBest = 0;
            toCompare = 0;
            findNodeToPrune(trainedDecisionTree);
            nodeToPrune.resetSplits(findLeafNode(nodeToPrune), null);
            nodeToPrune.setAttributeIndex(labelAttributeId);
            originalClassificationAccuracy = currentBest;
        } while (currentBest > originalClassificationAccuracy);
    }

    /**
     * Searches in a depth-first search for the node whose pruning improves the tree the most.
     *
     * @param subTree
     */
    public void findNodeToPrune(DecisionTreeNode subTree) {
        HashMap<String, DecisionTreeNode> map = subTree.getSplits();
        for (HashMap.Entry<String, DecisionTreeNode> test : map.entrySet()) {
            DecisionTreeNode child = test.getValue();
            if (child != null && child.getAttributeIndex() != labelAttributeId) {
                testPruneNode(child);
                findNodeToPrune(child);
            }
        }
    }

    /**
     * Receives a node, saves its splits and attribute index temporarily and now converts the node to a leaf node based on getProminentLabel().
     * The reference to this node gets saved if, its pruning causes the classification quality to get higher than the previous best.
     * After that, the node is reset to its original values.
     *
     * @param child
     */
    public void testPruneNode(DecisionTreeNode child) {
        splitsCache = child.getSplits();
        attributeIndexCache = child.getAttributeIndex();
        child.resetSplits(child.getProminentLabel(), null);
        child.setAttributeIndex(labelAttributeId);
        toCompare = Tester.test((List<CSVAttribute[]>) validationExamples, trainedDecisionTree, labelAttributeId);
        if (toCompare > currentBest) {
            currentBest = toCompare;
            nodeToPrune = child;
        }
        child.resetSplits(splitsCache);
        child.setAttributeIndex(attributeIndexCache);
    }

    /**
     * Sets the most common value of labelIndex in a depth search for all nodes (except for root and leaf nodes).
     *
     * @param subTree
     */
    private void setProminentLabel(DecisionTreeNode subTree) {
        HashMap<String, DecisionTreeNode> map = subTree.getSplits();
        for (HashMap.Entry<String, DecisionTreeNode> test : map.entrySet()) {
            DecisionTreeNode child = test.getValue();
            if (child != null && child.getAttributeIndex() != labelAttributeId) {
                child.setProminentLabel(findLeafNode(child));
                setProminentLabel(child);
            }
        }
    }

    /**
     * Receives a node, saves its splits and attribute index temporarily and now converts the node to a leaf node for all values of the label attribute,
     * it is registered for which value as a leaf node the classification-accuracy is highest and this value is saved in ProminentLabel.
     * After that the node receives its original values from the cache again.
     *
     * @param child
     * @return
     */
    private String findLeafNode(DecisionTreeNode child) {
        double accuracyWithBestLabel = 0;
        double searchBestLeafNode;
        String leafNodeValue = null;
        HashMap<String, DecisionTreeNode> splitsCache = child.getSplits();
        int attributeIndexCache = child.getAttributeIndex();
        for (int a = 0; a < range.size(); a++) {
            child.resetSplits((String) range.get(a), null);
            child.setAttributeIndex(labelAttributeId);
            searchBestLeafNode = Tester.test((List<CSVAttribute[]>) validationExamples, trainedDecisionTree, labelAttributeId);
            if (searchBestLeafNode > accuracyWithBestLabel) {
                accuracyWithBestLabel = searchBestLeafNode;
                leafNodeValue = (String) range.get(a);
            }
        }
        child.setAttributeIndex(attributeIndexCache);
        child.resetSplits(splitsCache);
        return leafNodeValue;
    }
}
