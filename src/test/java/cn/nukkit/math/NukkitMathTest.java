package cn.nukkit.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 */
class NukkitMathTest {

    @Test
    void floorDouble() {
        for (double i = -5; i <= 6; i += 0.001) {
            assertEquals((int) Math.floor(i), NukkitMath.floorDouble(i));
        }
    }

    @Test
    void ceilDouble() {
        for (double i = -5; i <= 6; i += 0.001) {
            assertEquals((int) Math.ceil(i), NukkitMath.ceilDouble(i));
        }
    }

    @Test
    void floorFloat() {
        for (float i = -5; i <= 6; i += 0.001) {
            assertEquals((int) Math.floor(i), NukkitMath.floorFloat(i));
        }
    }

    @Test
    void ceilFloat() {
        for (float i = -5; i <= 6; i += 0.001) {
            assertEquals((int) Math.ceil(i), NukkitMath.ceilFloat(i));
        }
    }
}
