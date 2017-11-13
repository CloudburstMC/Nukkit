package cn.nukkit.event.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Effect applyEffect;

    private final Entity entity;

    public PotionApplyEvent(Potion potion, Effect applyEffect, Entity entity) {
        super(potion);
        this.applyEffect = applyEffect;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Effect getApplyEffect() {
        return applyEffect;
    }

    public void setApplyEffect(Effect applyEffect) {
        this.applyEffect = applyEffect;
    }
}
