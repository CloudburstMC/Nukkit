package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.monster.Creeper;
import cn.nukkit.api.entity.weather.Lightning;
import cn.nukkit.api.event.Cancellable;

public class CreeperPowerEvent implements EntityEvent, Cancellable {
    private final Creeper creeper;
    private final PowerCause cause;
    private Lightning bolt;
    private boolean cancelled;

    public CreeperPowerEvent(final Creeper creeper, final Lightning bolt, final PowerCause cause) {
        this(creeper, cause);
        this.bolt = bolt;
    }

    public CreeperPowerEvent(final Creeper creeper, final PowerCause cause) {
        this.creeper = creeper;
        this.cause = cause;
    }

    @Override
    public Creeper getEntity() {
        return creeper;
    }

    /**
     * Gets the lightning bolt which is striking the Creeper.
     *
     * @return The Entity for the lightning bolt which is striking the Creeper
     */
    public Lightning getLightning() {
        return bolt;
    }

    /**
     * Gets the cause of the creeper being (un)powered.
     *
     * @return A PowerCause value detailing the cause of change in power.
     */
    public PowerCause getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
