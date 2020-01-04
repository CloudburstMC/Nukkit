package cn.nukkit.event.entity;

import cn.nukkit.entity.hostile.Creeper;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CreeperPowerEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PowerCause cause;
    private LightningBolt lightningBolt;

    public CreeperPowerEvent(final Creeper creeper, final LightningBolt lightningBolt, final PowerCause cause) {
        this(creeper, cause);
        this.lightningBolt = lightningBolt;
    }

    public CreeperPowerEvent(final Creeper creeper, final PowerCause cause) {
        this.entity = creeper;
        this.cause = cause;
    }

    @Override
    public Creeper getEntity() {
        return (Creeper) super.getEntity();
    }

    /**
     * Gets the lightning bolt which is striking the Creeper.
     *
     * @return The Entity for the lightning bolt which is striking the Creeper
     */
    public LightningBolt getLightningBolt() {
        return lightningBolt;
    }

    /**
     * Gets the cause of the creeper being (un)powered.
     *
     * @return A PowerCause value detailing the cause of change in power.
     */
    public PowerCause getCause() {
        return cause;
    }

    /**
     * An enum to specify the cause of the change in power
     */
    public enum PowerCause {

        /**
         * Power change caused by a lightning bolt
         * <p>
         * Powered state: true
         */
        LIGHTNING,
        /**
         * Power change caused by something else (probably a plugin)
         * <p>
         * Powered state: true
         */
        SET_ON,
        /**
         * Power change caused by something else (probably a plugin)
         * <p>
         * Powered state: false
         */
        SET_OFF
    }
}
