package com.suushiemaniac.cubing.bld.filter.condition;

public class IntCondition {
    public static IntCondition EXACT(int point) {
        return new IntCondition(point, point);
    }

    public static IntCondition PLUSMINUS(int point, int plusMinus) {
        return new IntCondition(point - plusMinus, point + plusMinus);
    }

    public static IntCondition INTERVAL(int min, int max) {
        return new IntCondition(min, max);
    }

    public static IntCondition MINIMUM(int min) {
        return new IntCondition(min, Integer.MAX_VALUE);
    }

    public static IntCondition MAXIMUM(int max) {
        return new IntCondition(Integer.MIN_VALUE, max);
    }

    public static IntCondition ANY() {
        return new IntCondition(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static IntCondition NONE() {
        return new IntCondition(0, 0);
    }

    private int min, max;

    private IntCondition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public void capMin(int minCap) {
        if (this.min < minCap) this.setMin(minCap);
    }

    public void capMax(int maxCap) {
        if (this.max > maxCap) this.setMax(maxCap);
    }

    public boolean evaluate(int compareTo) {
        return this.min <= compareTo && compareTo <= this.max;
    }

    public boolean isPrecise() {
        return this.min == this.max;
    }

    public int getInterval() {
        return this.max - this.min;
    }

    public float getAverage() {
        return (this.min + this.max) / 2.f;
    }

    public void setMax(int max) {
        this.max = max;
        if (this.min > max) this.min = max;
    }

    public void setMin(int min) {
        this.min = min;
        if (this.max < min) this.max = min;
    }

    @Override
    public String toString() {
        return this.min + ":" + this.max;
    }
}
