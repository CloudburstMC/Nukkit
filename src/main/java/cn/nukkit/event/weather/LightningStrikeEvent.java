package cn.nukkit.event.weather;

import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final LightningBolt lightningBolt;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LightningStrikeEvent(Level level, final LightningBolt lightningBolt) {
        super(level);
        this.lightningBolt = lightningBolt;
    }

    /**
     * Gets the bolt which is striking the earth.
     *
     * @return lightning entity
     */
    public LightningBolt getLightningBolt() {
        return lightningBolt;
    }

}
