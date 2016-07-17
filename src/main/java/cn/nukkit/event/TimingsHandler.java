package cn.nukkit.event;

import cn.nukkit.Server;
import cn.nukkit.command.defaults.TimingsCommand;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fromgate on 30.06.2016.
 */
public class TimingsHandler {

    private static final List<TimingsHandler> handlers = new ArrayList<>();
    private final String name;
    private final TimingsHandler parent;
    private long count;
    private long curCount;
    private long start;
    private long timingDepth;
    private long totalTime;
    private long curTickTotal;
    private long violations;

    public TimingsHandler(String name) {
        this(name, null);
    }

    public TimingsHandler(String name, TimingsHandler parent) {
        this.name = name;
        this.parent = parent;
        handlers.add(this);
    }

    public static List<String> getTimings() {
        List<String> strings = new ArrayList<>();
        strings.add("Minecraft");
        for (TimingsHandler timings : handlers) {
            long time = timings.totalTime;
            long count = timings.count;
            if (count == 0) continue;
            long avg = time / count;
            strings.add("   " + timings.name + " Time: " + time + " Count: " + count + " Avg: " + avg + " Violations: " + timings.violations);
        }
        strings.add("# Version " + Server.getInstance().getVersion());
        strings.add("# " + Server.getInstance().getName() + " " + Server.getInstance().getNukkitVersion());
        int entities = 0;
        int livingEntities = 0;
        for (Level level : Server.getInstance().getLevels().values()) {
            entities += level.getEntities().length;
            for (Entity e : level.getEntities())
                if (e instanceof EntityLiving) livingEntities++;
        }
        strings.add("# Entities " + entities);
        strings.add("# LivingEntities " + livingEntities);
        return strings;
    }

    public static void reload() {
        if (Server.getInstance().getPluginManager().useTimings()) {
            for (TimingsHandler timings : handlers)
                timings.reset();
        }
        TimingsCommand.timingStart = System.nanoTime();
    }

    public static void tick() {
        tick(true);
    }

    public static void tick(boolean measure) {
        if (Server.getInstance().getPluginManager().useTimings()) {
            if (measure) {
                handlers.forEach(timings -> {
                    if (timings.curTickTotal > 50000000L)
                        timings.violations = (long) ((double) timings.violations + Math.ceil((double) (timings.curTickTotal / 50000000L)));
                    timings.curTickTotal = 0;
                    timings.curCount = 0;
                    timings.timingDepth = 0;
                });
            } else {
                handlers.forEach(timings -> {
                    timings.totalTime -= timings.curTickTotal;
                    timings.count -= timings.curCount;
                    timings.curTickTotal = 0;
                    timings.curCount = 0;
                    timings.timingDepth = 0;
                });
            }
        }
    }


    public void startTiming() {
        if (Server.getInstance().getPluginManager().useTimings() && (++this.timingDepth == 1)) {
            this.start = System.nanoTime();
            if (this.parent != null && (++this.parent.timingDepth == 1)) {
                this.parent.start = this.start;
            }
        }
    }

    public void stopTiming() {
        if (Server.getInstance().getPluginManager().useTimings()) {
            if (--this.timingDepth != 0 || this.start == 0) return;
            long diff = System.nanoTime() - this.start;
            this.totalTime += diff;
            this.curTickTotal += diff;
            this.curCount++;
            this.count++;
            this.start = 0;
            if (this.parent != null) {
                this.parent.stopTiming();
            }
        }
    }

    public void reset() {
        this.count = 0;
        this.curCount = 0;
        this.violations = 0;
        this.curTickTotal = 0;
        this.start = 0;
        this.timingDepth = 0;
    }

    public void remove() {
        this.stopTiming();
        handlers.remove(this);
    }
}
