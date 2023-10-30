package de.uni_trier.wi2.pki;

public class Settings {


    static int labelIndex = 0;

    static String sourcePath = "src/main/resources/diabetes_binary_5050split_health_indicators_BRFSS2015-smaller.csv";
    static String xmlPath = "src/main/resources/xml/Run.xml";
    /**
     * This value should be true if the attribute names are on the first line.
     */
    static boolean ignoreHead = true;
    static String delimiter = ",";

    /**
     * true: potential already discrete attributes are recorded (it is recorded whether a column contains only integers), false: only categorical and continuous values are tested.
     */
    static boolean testIfDiscrete = false;

    /**
     * Counting starts at 1.
     */
    static int numberOfBins = 5;

    /**
     * Set which formula should be used to calculate the information gain 1 = EntropyUtils/ID3, 2 = Variance, 3 = GiniImpurity.
     */
    static int formularForGain = 1;
    /**
     * true: same interval size, false: same number of points per interval.
     */
    static boolean binningProcedure = true;
    /**
     * true: an individual number of bins is requested for each column, false: numberOfBins is always applied.
     */
    static boolean individualBins = false;

    /**
     * Activate or deactivate pruner.
     */
    static boolean prunerActive = true;

    static int numFolds = 5;

    public static void setLabelIndex(int labelIndex) {
        Settings.labelIndex = labelIndex;
    }

    public static String getDelimiter() {
        return delimiter;
    }

    public static int getLabelIndex() {
        return labelIndex;
    }

    public static String getXmlPath() {
        return xmlPath;
    }

    public static String getSourcePath() {
        return sourcePath;
    }

    public static boolean isTestIfDiscrete() {
        return testIfDiscrete;
    }

    public static boolean isIgnoreHead() {
        return ignoreHead;
    }

    public static int getNumberOfBins() {
        return numberOfBins;
    }

    public static boolean isIndividualBins() {
        return individualBins;
    }

    public static boolean isBinningProcedure() {
        return binningProcedure;
    }

    public static int getNumFolds() {
        return numFolds;
    }

    public static int getFormularForGain() {
        return formularForGain;
    }

    public static boolean isPrunerActive() {
        return prunerActive;
    }

}
