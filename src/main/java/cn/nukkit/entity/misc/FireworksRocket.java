package cn.nukkit.entity.misc;

import cn.nukkit.entity.Entity;
import com.nukkitx.nbt.tag.CompoundTag;

public interface FireworksRocket extends Entity {

    int getLife();

    void setLife(int life);

    int getLifetime();

    void setLifetime(int lifetime);

    CompoundTag getFireworkData();

    void setFireworkData(CompoundTag tag);
}
