package cn.nukkit.event.potion;

import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityPotion thrownPotion;

    public PotionCollideEvent(Potion potion, EntityPotion thrownPotion) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityPotion getThrownPotion() {
        return thrownPotion;
    }
}
