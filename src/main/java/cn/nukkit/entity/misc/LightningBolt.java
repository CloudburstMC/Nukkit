package cn.nukkit.entity.misc;

import cn.nukkit.entity.Entity;

public interface LightningBolt extends Entity {

    boolean isEffect();

    void setEffect(boolean effect);
}
