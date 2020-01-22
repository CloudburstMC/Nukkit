package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Projectile;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

public interface FishingHook extends Projectile {

    @Nullable
    Item getRod();

    void setRod(@Nullable Item rod);

    void reelLine();
}
