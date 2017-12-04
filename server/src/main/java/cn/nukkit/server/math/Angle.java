package cn.nukkit.server.math;

import java.util.Locale;

import static java.lang.Math.PI;

/**
 * Copyright 2017 lmlstarqaq
 * All rights reserved.
 */
public final class Angle implements Comparable<Angle> {

    public static Angle fromDegree(float floatDegree) {
        return new Angle(floatDegree, true);
    }

    public static Angle fromDegree(double doubleDegree) {
        return new Angle(doubleDegree, true);
    }

    public static Angle fromRadian(float floatRadian) {
        return new Angle(floatRadian, false);
    }

    public static Angle fromRadian(double doubleRadian) {
        return new Angle(doubleRadian, false);
    }

    public static Angle asin(double v) {
        return fromRadian(Math.asin(v));
    }

    public static Angle acos(double v) {
        return fromRadian(Math.acos(v));
    }

    public static Angle atan(double v) {
        return fromRadian(Math.atan(v));
    }

    public double sin() {
        return Math.sin(asDoubleRadian());
    }

    public double cos() {
        return Math.cos(asDoubleRadian());
    }

    public double tan() {
        return Math.tan(asDoubleRadian());
    }

    public float asFloatRadian() {
        if (isOriginDouble) {
            if (isDegree) return (float) (doubleValue * PI / 180.0);
            else return (float) doubleValue;
        } else {
            if (isDegree) return floatValue * (float) PI / 180.0f;
            else return floatValue;
    }
    }

    public double asDoubleRadian() {
        if (isOriginDouble) {
            if (isDegree) return doubleValue * PI / 180.0;
            else return doubleValue;
        } else {
            if (isDegree) return floatValue * PI / 180.0;
            else return floatValue;
    }
    }

    public float asFloatDegree() {
        if (isOriginDouble) {
            if (isDegree) return (float) doubleValue;
            else return (float) (doubleValue * 180.0 / PI);
        } else {
            if (isDegree) return floatValue;
            else return floatValue * 180.0f / (float) PI;
    }
    }

    public double asDoubleDegree() {
        if (isOriginDouble) {
            if (isDegree) return doubleValue;
            else return doubleValue * 180.0 / PI;
        } else {
            if (isDegree) return floatValue;
            else return floatValue * 180.0 / PI;
    }
    }

    public static int compare(Angle a, Angle b) {
        return a.compareTo(b);
    }

  /* -- Override -- */

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Angle[%s, %f%s = %f%s] [%d]",
                isOriginDouble ? "Double" : "Float",
                isOriginDouble ? doubleValue : floatValue,
                isDegree ? "deg" : "rad",
                isDegree ? (isOriginDouble ? asDoubleRadian() : asFloatRadian()) :
                        (isOriginDouble ? asDoubleDegree() : asFloatDegree()),
                isDegree ? "rad" : "deg",
                hashCode()
        );
    }

    @Override
    public int compareTo(Angle o) {
        return Double.compare(asDoubleRadian(), o.asDoubleRadian());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Angle && this.compareTo((Angle) obj) == 0;
    }

    @Override
    public int hashCode() {
        int hash;
        if (isOriginDouble) hash = Double.hashCode(doubleValue);
        else hash = Float.hashCode(floatValue);
        if (isDegree) hash = hash ^ 0xABCD1234;
        return hash;
    }

  /* -- Internal Part -- */

    private final float floatValue;
    private final double doubleValue;
    private final boolean isDegree, isOriginDouble;

    private Angle(float floatValue, boolean isDegree) {
        this.isOriginDouble = false;
        this.floatValue = floatValue;
        this.doubleValue = 0.0;
        this.isDegree = isDegree;
    }

    private Angle(double doubleValue, boolean isDegree) {
        this.isOriginDouble = true;
        this.floatValue = 0.0f;
        this.doubleValue = doubleValue;
        this.isDegree = isDegree;
    }

}
