package org.cifasis.mc1;

/**
 * An Int is a mutable wrapper for int.
 * Created by Cristian on 15/06/15.
 */
public class Int extends Number {

    private int value;

    public Int(int value) {
        this.value = value;
    }

    public void set(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void add(Int that) {
        this.value += that.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Int)) return false;
        Int anInt = (Int) o;
        return com.google.common.base.Objects.equal(value, anInt.value);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(value);
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public String toString() {
        return "" + value;

    }
}
