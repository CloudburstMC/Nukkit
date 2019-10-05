package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Utils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
public class LevelManager implements Closeable {
    private final Server server;
    private final Set<Level> levels = new HashSet<>();
    private final Map<String, Level> levelIds = new HashMap<>();
    private volatile Level defaultLevel;

    public LevelManager(Server server) {
        this.server = server;
    }

    public synchronized void register(Level level) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkArgument(level.getServer() == this.server, "Level did not come from this server");
        Preconditions.checkArgument(!levels.contains(level), "level already registered");

        LevelLoadEvent event = new LevelLoadEvent(level);
        this.server.getPluginManager().callEvent(event);

        this.levels.add(level);
        this.levelIds.put(level.getId(), level);
    }

    public boolean deregister(Level level) {
        return deregister(level, false);
    }

    public synchronized boolean deregister(Level level, boolean force) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkArgument(levels.contains(level), "level not registered");

        LevelUnloadEvent event = new LevelUnloadEvent(level);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled() && !force) {
            return false;
        } else {
            this.levelIds.remove(level.getId());
            return levels.remove(level);
        }
    }

    @Nullable
    public synchronized Level getLevel(String id) {
        return this.levelIds.get(id);
    }

    @Nullable
    public synchronized Level getLevelByName(String name) {
        for (Level level : this.levels) {
            if (level.getName().equals(name)) {
                return level;
            }
        }
        return null;
    }

    public Level getDefaultLevel() {
        return defaultLevel;
    }

    public synchronized void setDefaultLevel(Level level) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkArgument(levels.contains(level), "level not registered");

        this.defaultLevel = level;
    }

    public synchronized Set<Level> getLevels() {
        return ImmutableSet.copyOf(levels);
    }

    public synchronized void save() {
        this.levels.forEach(Level::save);
    }

    @Override
    public synchronized void close() {
        for (Level level : this.levels) {
            level.close();
        }
    }

    public void tick(int currentTick) {
        for (Level level : this.levels) {
            try {
                long levelTime = System.currentTimeMillis();
                level.doTick(currentTick);
                int tickMs = (int) (System.currentTimeMillis() - levelTime);
                level.tickRateTime = tickMs;

                if (server.isAutoTickRate()) {
                    if (tickMs < 50 && level.getTickRate() > server.getBaseTickRate()) {
                        int r;
                        level.setTickRate(r = level.getTickRate() - 1);
                        if (r > server.getBaseTickRate()) {
                            level.tickRateCounter = level.getTickRate();
                        }
                        log.debug("Raising level \"" + level.getName() + "\" tick rate to " + level.getTickRate() + " ticks");
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() == server.getBaseTickRate()) {
                            level.setTickRate(Math.max(server.getBaseTickRate() + 1, Math.min(server.getAutoTickRateLimit(), tickMs / 50)));
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < server.getAutoTickRateLimit()) {
                            level.setTickRate(level.getTickRate() + 1);
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }

                if (currentTick % 100 == 0) {
                    level.doChunkGarbageCollection();
                }
            } catch (Exception e) {
                log.error(server.getLanguage().translateString("nukkit.level.tickError",
                        new String[]{level.getId(), Utils.getExceptionMessage(e)}));
            }
        }
    }
}
