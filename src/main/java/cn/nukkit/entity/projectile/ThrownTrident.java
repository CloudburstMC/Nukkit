package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Projectile;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

public interface ThrownTrident extends Projectile {

    Item getTrident();

    void setTrident(@Nonnull Item trident);
}
