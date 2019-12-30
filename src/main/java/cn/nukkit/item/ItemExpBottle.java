package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/25 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemExpBottle extends ProjectileItem {

    public ItemExpBottle(Identifier id) {
        super(id);
    }

    @Override
    public String getProjectileEntityType() {
        return "ThrownExpBottle";
    }

    @Override
    public float getThrowForce() {
        return 1f;
    }

}
