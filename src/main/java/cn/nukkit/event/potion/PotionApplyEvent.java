package cn.nukkit.event.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Potion;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Entity entity;

    public PotionApplyEvent(Potion potion, Entity entity) {
        super(potion);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
