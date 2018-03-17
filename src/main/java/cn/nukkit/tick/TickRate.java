package cn.nukkit.tick;

import cn.nukkit.math.MathHelper;
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
    public float currentLoad = 0.0f;

    public long lastUpdate = -1;

    public float[] tpsCounts = new float[20];

    /**
     * Notify the tick rate counter that the next tick is starting
     */
    synchronized void update() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdate == -1) {
            lastUpdate = currentTime;
            return;
        }
        long timeDiff = currentTime - lastUpdate;
        float tickTime = timeDiff / 20f;
        if (tickTime == 0) {
            tickTime = 50f;
        }
        float tps = 50f / tickTime;
        if (tps > 20.0f) {
            tps = 20.0f;
        }
        for (int i = tpsCounts.length - 1; i > 0; i--) {
            tpsCounts[i] = tpsCounts[i - 1]; //move everything over 1
        }
        tpsCounts[0] = tps;

        double total = 0.0d;
        for (float f : tpsCounts) {
            total += f;
        }
        total /= (double) tpsCounts.length;

        if (total > 20.0f) {
            total = 20.0f;
        }

        currentTps = (float) NukkitMath.round(total, 2);
        lastUpdate = currentTime;
    }

    synchronized void endTick() {
        currentLoad = Math.max(0.0f, Math.min(1.0f, (System.currentTimeMillis() - lastUpdate) / 50f)) * 100f;
    }

    public void reset() {
        for (int i = 0; i < tpsCounts.length; i++) {
            tpsCounts[i] = 20.0f;
        }
        currentTps = 20.0f;
    }
}
