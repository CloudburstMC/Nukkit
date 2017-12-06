package cn.nukkit.api.event.potion;

import cn.nukkit.server.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.api.event.potion in project nukkit
 */
public abstract class PotionEvent extends Event {

    private Potion potion;

    public PotionEvent(Potion potion) {
        this.potion = potion;
    }

    public Potion getPotion() {
        return potion;
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
    }

}
