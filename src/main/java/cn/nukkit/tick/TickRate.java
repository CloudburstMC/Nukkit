package cn.nukkit.tick;

import cn.nukkit.math.NukkitMath;

import java.text.DecimalFormat;

/**
 * @author DaPorkchop_
 *
 * Literally just a copypaste of https://github.com/Team-Pepsi/pepsimod/blob/master/src/main/java/net/daporkchop/pepsimod/misc/TickRate.java
 * I hereby give permission to use this in Nukkit, whatever
 * I mean I **am** the one who copypasted it all
 * and it's pretty simple code
 * whatever
 * note to self: stop rambling to self in javadoc headers
 */
public class TickRate {
    public static final TickRate INSTANCE = new TickRate();

    private TickRate()  {
    }

    public float currentTps = 20.0f;

    public long lastUpdate = -1;

    public float[] tpsCounts = new float[20];

    /**
     * Notify the tick rate counter that the next tick is starting
     */
    public synchronized void update() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdate == -1) {
            lastUpdate = currentTime;
            return;
        }
        long timeDiff = currentTime - lastUpdate;
        float tickTime = timeDiff / 20;
        if (tickTime == 0) {
            tickTime = 50;
        }
        float tps = 1000 / tickTime;
        if (tps > 20.0f) {
            tps = 20.0f;
        }
        for (int i = tpsCounts.length - 1; i > 0; i--) {
            tpsCounts[i] = tpsCounts[i - 1]; //move everything over 1
        }
        tpsCounts[0] = tps;

        double total = 0.0;
        for (float f : tpsCounts) {
            total += f;
        }
        total /= tpsCounts.length;

        if (total > 20.0) {
            total = 20.0;
        }

        currentTps = (float) NukkitMath.round(total, 2);
        lastUpdate = currentTime;
    }

    public void reset() {
        for (int i = 0; i < tpsCounts.length; i++) {
            tpsCounts[i] = 20.0f;
        }
        currentTps = 20.0f;
    }
}
