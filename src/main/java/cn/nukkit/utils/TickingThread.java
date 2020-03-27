package cn.nukkit.utils;

import cn.nukkit.math.NukkitMath;

public abstract class TickingThread extends Thread {

    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int currentTick;
    private float maxTick;
    private float maxUse;
    private volatile boolean isRunning = true;

    public TickingThread() {
        super();
    }

    public TickingThread(String name) {
        super(name);
    }

    public TickingThread(ThreadGroup group, String name) {
        super(group, name);
    }

    @Override
    public void run() {
        long nextTick = -1;

        while (this.isRunning) {
            long tickTime = System.currentTimeMillis();

            long time = tickTime - nextTick;
            if (time < -25) {
                try {
                    Thread.sleep(Math.max(5, -time - 25));
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            long beforeTick = System.nanoTime();

            if ((tickTime - nextTick) >= -25) {
                this.tick(this.currentTick++);
            }

            long afterTick = System.nanoTime();

            float tick = (float) Math.min(20, 1000000000 / Math.max(1000000, ((double) afterTick - beforeTick)));
            float use = (float) Math.min(1, ((double) (afterTick - beforeTick)) / 50000000);

            if (this.maxTick > tick) {
                this.maxTick = tick;
            }

            if (this.maxUse < use) {
                this.maxUse = use;
            }

            System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
            this.tickAverage[this.tickAverage.length - 1] = tick;

            System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
            this.useAverage[this.useAverage.length - 1] = use;

            if ((nextTick - tickTime) < -1000) {
                nextTick = tickTime;
            } else {
                nextTick += 50;
            }

            long current = System.currentTimeMillis();

            if (nextTick - 0.1 > current) {
                long allocated = nextTick - current - 1;

                if (allocated > 0) {
                    try {
                        Thread.sleep(allocated, 900000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        }
    }

    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NukkitMath.round(sum / count, 2);
    }

    public float getTickUsage() {
        return (float) NukkitMath.round(this.maxUse * 100, 2);
    }

    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void terminate() {
        this.isRunning = false;
    }

    protected abstract void tick(int currentTick);
}
