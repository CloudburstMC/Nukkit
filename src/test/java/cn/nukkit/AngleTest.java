package cn.nukkit;

import cn.nukkit.math.Angle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.nukkit.math.Angle.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Copyright 2017 lmlstarqaq
 * All rights reserved.
 */

@DisplayName("Angle")
class AngleTest {

	@DisplayName("Angle operations")
	@Test
	void testAngle() {
		Angle rd1 = fromRadian(1.0);
		Angle rf1 = fromRadian(1.0f);
		Angle dd1 = fromDegree(180.0);
		Angle df1 = fromDegree(180.0f);

		assertEquals("57.29578\t57.29577951308232\t1.0\t1.0", rd1.asFloatDegree() + "\t" + rd1.asDoubleDegree() + "\t" + rd1.asFloatRadian() + "\t" + rd1.asDoubleRadian());
		assertEquals("57.295776\t57.29577951308232\t1.0\t1.0", rf1.asFloatDegree() + "\t" + rf1.asDoubleDegree() + "\t" + rf1.asFloatRadian() + "\t" + rf1.asDoubleRadian());
		assertEquals("180.0\t180.0\t3.1415927\t3.141592653589793", dd1.asFloatDegree() + "\t" + dd1.asDoubleDegree() + "\t" + dd1.asFloatRadian() + "\t" + dd1.asDoubleRadian());
		assertEquals("180.0\t180.0\t3.1415927\t3.141592653589793", df1.asFloatDegree() + "\t" + df1.asDoubleDegree() + "\t" + df1.asFloatRadian() + "\t" + df1.asDoubleRadian());
		assertEquals("Angle[Double, 1.000000rad = 57.295780deg] [1072693248]", rd1.toString());
		assertEquals("Angle[Float, 1.000000rad = 57.295776deg] [1065353216]", rf1.toString());
		assertEquals("Angle[Double, 180.000000deg = 3.141593rad] [-341077452]", dd1.toString());
		assertEquals("Angle[Float, 180.000000deg = 3.141593rad] [-386330060]", df1.toString());

		Angle rd2 = fromRadian(200.0);
		Angle rf2 = fromRadian(200.0f);
		Angle dd2 = fromDegree(23333.33);
		Angle df2 = fromDegree(23333.33f);

		assertEquals("11459.156\t11459.155902616465\t200.0\t200.0", rd2.asFloatDegree() + "\t" + rd2.asDoubleDegree() + "\t" + rd2.asFloatRadian() + "\t" + rd2.asDoubleRadian());
		assertEquals("11459.155\t11459.155902616465\t200.0\t200.0", rf2.asFloatDegree() + "\t" + rf2.asDoubleDegree() + "\t" + rf2.asFloatRadian() + "\t" + rf2.asDoubleRadian());
		assertEquals("23333.33\t23333.33\t407.24344\t407.24343395436847", dd2.asFloatDegree() + "\t" + dd2.asDoubleDegree() + "\t" + dd2.asFloatRadian() + "\t" + dd2.asDoubleRadian());
		assertEquals("23333.33\t23333.330078125\t407.24344\t407.243435317907", df2.asFloatDegree() + "\t" + df2.asDoubleDegree() + "\t" + df2.asFloatRadian() + "\t" + df2.asDoubleRadian());

		assertEquals("0.8414709848078965\t0.5403023058681398\t1.5574077246549023", rd1.sin() + "\t" + rd1.cos() + "\t" +rd1.tan());
		assertEquals("1.2246467991473532E-16\t-1.0\t-1.2246467991473532E-16", dd1.sin() + "\t" + dd1.cos() + "\t" +dd1.tan());

		Angle asin1 = asin(1);
		Angle asin2 = asin(1.0/2.0);
		Angle acos1 = acos(1);
		Angle acos2 = acos(1.0/2.0);
		Angle atan1 = atan(1);
		Angle atan2 = atan(1.0/2.0);

		assertEquals("Angle[Double, 1.570796rad = 90.000000deg] [1807551715]\tAngle[Double, 0.000000rad = 0.000000deg] [0]\tAngle[Double, 0.785398rad = 45.000000deg] [1806503139]",
				asin1.toString() + "\t" + acos1.toString() + "\t" + atan1.toString());
		assertEquals("Angle[Double, 0.523599rad = 30.000000deg] [130921012]\tAngle[Double, 1.047198rad = 60.000000deg] [131969588]\tAngle[Double, 0.463648rad = 26.565051deg] [985405224]",
				asin2.toString() + "\t" + acos2.toString() + "\t" + atan2.toString());

		assertEquals(1, compare(asin(2.0/3.0), asin(1.0/2.0)));

		assertEquals("Angle[Double, 22.000000rad = 1260.507149deg] [1077280768]\tAngle[Float, 22.000000rad = 1260.507080deg] [1102053376]",
				fromRadian(22.0d).toString() + "\t" + fromRadian(22.0f).toString());
		assertEquals("Angle[Double, 1.000000rad = 57.295780deg] [1072693248]\tAngle[Double, 57.295780deg = 1.000000rad] [-236816880]",
						fromRadian(1.0d).toString() + "\t" + fromDegree(57.29577951308232d).toString());
		assertEquals("Angle[Double, 1.000000rad = 57.295780deg] [1072693248]\tAngle[Double, 1.000000deg = 0.017453rad] [-1807936972]",
				fromRadian(1.0d).toString() + "\t" + fromDegree(1.0d).toString());
	}
}
