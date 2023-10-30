package de.uni_trier.wi2.pki.io.attr;

public class Discrete implements CSVAttribute {

    private String value;
    private int backUpValue;
    private int attributIndex;

    @Override
    public void setValue(Object value) {
        this.value = (String) value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setBackUpValue(Object backUpValue) {
        this.backUpValue = (int) backUpValue;
    }

    @Override
    public Object getBackUpValue() {
        return backUpValue;
    }

    @Override
    public void setAttributIndex(Object attributIndex) {
        this.attributIndex = (int) attributIndex;
    }

    @Override
    public Object getAttributIndex() {
        return attributIndex;
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
