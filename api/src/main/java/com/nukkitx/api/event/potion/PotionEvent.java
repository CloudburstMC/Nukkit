package com.nukkitx.api.event.potion;

import com.nukkitx.api.entity.misc.Potion;
import com.nukkitx.api.event.Event;

public interface PotionEvent extends Event {

    Potion getPotion();

    void setPotion(Potion potion);

}
