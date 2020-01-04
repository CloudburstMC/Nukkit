package cn.nukkit.event.potion;

import cn.nukkit.entity.projectile.SplashPotion;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final SplashPotion thrownPotion;

    public PotionCollideEvent(Potion potion, SplashPotion thrownPotion) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public SplashPotion getThrownPotion() {
        return thrownPotion;
    }
}
