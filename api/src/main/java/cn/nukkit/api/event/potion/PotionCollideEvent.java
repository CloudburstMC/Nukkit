package cn.nukkit.api.event.potion;

import cn.nukkit.api.entity.misc.Potion;
import cn.nukkit.api.event.Cancellable;

public class PotionCollideEvent implements PotionEvent, Cancellable {
    private final Potion thrownPotion;
    private Potion potion;
    private boolean cancelled;

    public PotionCollideEvent(Potion potion, Potion thrownPotion) {
        this.potion = potion;
        this.thrownPotion = thrownPotion;
    }

    public Potion getThrownPotion() {
        return thrownPotion;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Potion getPotion() {
        return potion;
    }

    @Override
    public void setPotion(Potion potion) {
        this.potion = potion;
    }
}
