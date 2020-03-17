package cn.nukkit.blockentity;

import cn.nukkit.item.Item;

public interface Jukebox extends BlockEntity {

    Item getRecordItem();

    void setRecordItem(Item recordItem);

    void play();

    void stop();

    void dropItem();
}
