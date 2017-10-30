package cn.nukkit.item;

/**
 * Created on 2015/12/25 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemExpBottle extends ProjectileItem {

    public ItemExpBottle() {
        this(0, 1);
    }

    public ItemExpBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemExpBottle(Integer meta, int count) {
        super(EXPERIENCE_BOTTLE, meta, count, "Bottle o' Enchanting");
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
