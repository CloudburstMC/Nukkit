package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * Called when a potion effect is modified on an entity.
 * If the event is cancelled, no change will be made on the entity.
 */
public class EntityPotionEffectEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the old potion effect of the changed type, which will be removed. Null if Action == ADDED.
     */
    @Getter
    @Nullable
    private final Effect oldEffect;
    /**
     * Gets new potion effect of the changed type to be applied. Null if Action == REMOVED.
     */
    @Getter
    @Nullable
    private final Effect newEffect;
    /**
     * Gets the action which will be performed on the potion effect type.
     */
    @Getter
    private final Action action;
    /**
     * Gets the cause why the effect has changed.
     */
    @Getter
    private final Cause cause;

    public EntityPotionEffectEvent(Entity entity, Effect oldEffect, Effect newEffect, Action action, Cause cause) {
        this.entity = entity;
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
        this.action = action;
        this.cause = cause;
    }

    /**
     * An enum to specify the action to be performed.
     */
    public enum Action {

        /**
         * When the potion effect is added because the entity didn't have it's type.
         */
        ADDED,
        /**
         * When the entity already had the potion effect type, but the effect is changed.
         */
        CHANGED,
        /**
         * When the potion effect type is completely removed.
         */
        REMOVED
    }

    /**
     * An enum to specify the cause why an effect was changed.
     */
    public enum Cause {

        /**
         * When the entity stands inside an area effect cloud.
         */
        AREA_EFFECT_CLOUD,
        /**
         * When the entity is hit by an spectral or tipped arrow.
         */
        ARROW,
        /**
         * When the entity is inflicted with a potion effect due to an entity
         * attack (e.g. a cave spider or a shulker bullet).
         */
        ATTACK,
        /**
         * When an entity gets the effect from an axolotl.
         */
        AXOLOTL,
        /**
         * When beacon effects get applied due to the entity being nearby.
         */
        BEACON,
        /**
         * When a potion effect is changed due to the /effect command.
         */
        COMMAND,
        /**
         * When the entity gets the effect from a conduit.
         */
        CONDUIT,
        /**
         * When a conversion from a villager zombie to a villager is started or
         * finished.
         */
        CONVERSION,
        /**
         * When all effects are removed due to death (Note: This is called on
         * respawn, so it's player only!)
         */
        DEATH,
        /**
         * When the entity gets the effect from a dolphin.
         */
        DOLPHIN,
        /**
         * When the effect was removed due to expiration.
         */
        EXPIRATION,
        /**
         * When an effect is inflicted due to food (e.g. when a player eats or a
         * cookie is given to a parrot).
         */
        FOOD,
        /**
         * When all effects are removed due to a bucket of milk.
         */
        MILK,
        /**
         * When a player gets bad omen after killing a patrol captain.
         */
        PATROL_CAPTAIN,
        /**
         * When a potion effect is modified through the plugin methods.
         */
        PLUGIN,
        /**
         * When the entity drinks a potion.
         */
        POTION_DRINK,
        /**
         * When the entity is inflicted with an effect due to a splash potion.
         */
        POTION_SPLASH,
        /**
         * When a spider gets effects when spawning on hard difficulty.
         */
        SPIDER_SPAWN,
        /**
         * When the entity gets effects from a totem item saving it's life.
         */
        TOTEM,
        /**
         * When the entity gets water breathing by wearing a turtle helmet.
         */
        TURTLE_HELMET,
        /**
         * When the Cause is missing.
         */
        UNKNOWN,
        /**
         * When a villager gets regeneration after a trade.
         */
        VILLAGER_TRADE,
        /**
         * When an entity gets the effect from a warden.
         */
        WARDEN,
        /**
         * When an entity comes in contact with a wither rose.
         */
        WITHER_ROSE,
        /**
         * When nearby elder guardian gives mining fatigue to player.
         */
        ELDER_GUARDIAN
    }
}
