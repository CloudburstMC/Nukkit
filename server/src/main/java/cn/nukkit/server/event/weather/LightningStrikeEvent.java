package cn.nukkit.server.event.weather;

import cn.nukkit.server.entity.weather.EntityLightningStrike;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityLightningStrike bolt;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LightningStrikeEvent(Level level, final EntityLightningStrike bolt) {
        super(level);
        this.bolt = bolt;
    }

    /**
     * * Gets the bolt which is striking the earth.
     * * @return lightning entity
     */
    public EntityLightningStrike getLightning() {
        return bolt;
    }

}
