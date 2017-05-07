package cn.nukkit.test;

import cn.nukkit.math.Angle;

import static cn.nukkit.math.Angle.*;

/**
 * Copyright 2017 lmlstarqaq
 * All rights reserved.
 */
public class AngleTest {
  public static void main(String[] args) {
    Angle rd1 = fromRadian(1.0);
    Angle rf1 = fromRadian(1.0f);
    Angle dd1 = fromDegree(180.0);
    Angle df1 = fromDegree(180.0f);

    System.out.println(rd1.asFloatDegree() + "\t" + rd1.asDoubleDegree() + "\t" + rd1.asFloatRadian() + "\t" + rd1.asDoubleRadian());
    System.out.println(rf1.asFloatDegree() + "\t" + rf1.asDoubleDegree() + "\t" + rf1.asFloatRadian() + "\t" + rf1.asDoubleRadian());
    System.out.println(dd1.asFloatDegree() + "\t" + dd1.asDoubleDegree() + "\t" + dd1.asFloatRadian() + "\t" + dd1.asDoubleRadian());
    System.out.println(df1.asFloatDegree() + "\t" + df1.asDoubleDegree() + "\t" + df1.asFloatRadian() + "\t" + df1.asDoubleRadian());
    System.out.println(rd1.toString());
    System.out.println(rf1.toString());
    System.out.println(dd1.toString());
    System.out.println(df1.toString());

    Angle rd2 = fromRadian(200.0);
    Angle rf2 = fromRadian(200.0f);
    Angle dd2 = fromDegree(23333.33);
    Angle df2 = fromDegree(23333.33f);

    System.out.println(rd2.asFloatDegree() + "\t" + rd2.asDoubleDegree() + "\t" + rd2.asFloatRadian() + "\t" + rd2.asDoubleRadian());
    System.out.println(rf2.asFloatDegree() + "\t" + rf2.asDoubleDegree() + "\t" + rf2.asFloatRadian() + "\t" + rf2.asDoubleRadian());
    System.out.println(dd2.asFloatDegree() + "\t" + dd2.asDoubleDegree() + "\t" + dd2.asFloatRadian() + "\t" + dd2.asDoubleRadian());
    System.out.println(df2.asFloatDegree() + "\t" + df2.asDoubleDegree() + "\t" + df2.asFloatRadian() + "\t" + df2.asDoubleRadian());

    System.out.println(rd1.sin() + "\t" + rd1.cos() + "\t" +rd1.tan());
    System.out.println(dd1.sin() + "\t" + dd1.cos() + "\t" +dd1.tan());

    Angle asin1 = asin(1);
    Angle asin2 = asin(1.0/2.0);
    Angle acos1 = acos(1);
    Angle acos2 = acos(1.0/2.0);
    Angle atan1 = atan(1);
    Angle atan2 = atan(1.0/2.0);

    System.out.println(asin1.toString() + "\t" + acos1.toString() + "\t" + atan1.toString());
    System.out.println(asin2.toString() + "\t" + acos2.toString() + "\t" + atan2.toString());

    System.out.println(compare(asin(2.0/3.0), asin(1.0/2.0)));

    System.out.println(fromRadian(22.0d).toString() + "\t" + fromRadian(22.0f).toString());
    System.out.println(fromRadian(1.0d).toString() + "\t" + fromDegree(57.29577951308232d).toString());
    System.out.println(fromRadian(1.0d).toString() + "\t" + fromDegree(1.0d).toString());
  }
}
