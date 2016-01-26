package cn.nukkit.event.potion;

import cn.nukkit.entity.Potion;
import cn.nukkit.entity.ThrownPotion;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private ThrownPotion thrownPotion;

    public PotionCollideEvent(Potion potion, ThrownPotion thrownPotion) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public ThrownPotion getThrownPotion() {
        return thrownPotion;
    }
}
