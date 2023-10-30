package de.uni_trier.wi2.pki.io.attr;

/**
 * Generic interface for a single attribute value of a CSV dataset.
 *
 * @param <T> the data type of the CSV attribute
 */
public interface CSVAttribute<T> extends Comparable<CSVAttribute<T>>, Cloneable {

    /**
     * Set the value for this attribute.
     *
     * @param value the value
     */
    void setValue(T value);

    /**
     * Get the value for this attribute.
     *
     * @return the value
     */
    T getValue();

    void setBackUpValue(T backUpValue); //Added

    T getBackUpValue();               //Added

    void setAttributIndex(T attributIndex); //Added

    T getAttributIndex();//Added

    /**
     * Clone this attribute with its value.
     *
     * @return the cloned attribute
     */
    Object clone();

}
