package cn.nukkit.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 */
class NukkitRandomTest {
    NukkitRandom random;
    
    @Test
    void nextRange() {
        Boolean[] matched = new Boolean[8-2 + 1];
        Arrays.fill(matched, Boolean.FALSE);
        for (int i = 0; i < 1000; i++) {
            int rand = random.nextRange(2, 8);
            assertThat(rand).isIn(2, 3, 4, 5, 6, 7, 8);
            matched[rand-2] = Boolean.TRUE;
        }
        Boolean[] expected = new Boolean[matched.length];
        Arrays.fill(expected, Boolean.TRUE);
        assertEquals(Arrays.asList(expected), Arrays.asList(matched));
    }

    @BeforeEach
    void setUp() {
        random = new NukkitRandom(ThreadLocalRandom.current().nextLong()); 
    }
}
