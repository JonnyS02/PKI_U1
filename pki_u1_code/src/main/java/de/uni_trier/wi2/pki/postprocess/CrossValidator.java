package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.Settings;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains util methods for performing a cross-validation.
 */
public class CrossValidator {
    private static final DecimalFormat df = new DecimalFormat("0.0000");
    private static boolean prunerActive = Settings.isPrunerActive();
    private static double accumulatedClassAccuracy = 0;
    private static DecisionTreeNode node = new DecisionTreeNode();
    private static double currentBest = 0;

    /**
     * Performs a cross-validation with the specified dataset and the function to train the model.
     *
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     */
    public static DecisionTreeNode performCrossValidation(List<CSVAttribute[]> dataset, int labelAttribute, BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> trainFunction, int numFolds) {

        int dataAmount = dataset.size();
        for (int i = 0; i < numFolds; i++) {
            double foldSize = (double) (dataAmount - 1) / numFolds;
            int minIndexTesting = (int) (i * foldSize);
            int maxIndexTesting = (int) ((i + 1) * foldSize);

            List<CSVAttribute[]> formatedDataset = Main.getFormattedAttributes();
            List<CSVAttribute[]> datasetForTest = dataset.subList(minIndexTesting, maxIndexTesting);
            List<CSVAttribute[]> datasetForTrain = Stream.concat(formatedDataset.subList(0, minIndexTesting).stream(), formatedDataset.subList(maxIndexTesting, dataAmount - 1).stream()).collect(Collectors.toList());
            startValidation(datasetForTest, datasetForTrain, trainFunction, labelAttribute, i);
        }

        System.out.println(df.format(accumulatedClassAccuracy / numFolds) + "% of training data was correct labeled");
        System.out.println("------------------------------");
        return node;
    }

    private static void startValidation(List<CSVAttribute[]> datasetForTest, List<CSVAttribute[]> datasetForTrain, BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> trainFunction, int labelAttribute, int i) {
        System.out.println("Now pruning -- numFold: " + i);
        System.out.println("------------------------------");
        DecisionTreeNode rootOfTree = trainFunction.apply(datasetForTrain, datasetForTrain.get(0).length - 1);

        if (prunerActive) {
            ReducedErrorPruner pruner = new ReducedErrorPruner();
            pruner.prune(rootOfTree, datasetForTest, labelAttribute);
        }

        double classAccuracy = Tester.test(datasetForTest, rootOfTree, labelAttribute);
        if(classAccuracy>currentBest){
            node = rootOfTree;
            currentBest = classAccuracy;
        }
        accumulatedClassAccuracy += classAccuracy;
    }

}
