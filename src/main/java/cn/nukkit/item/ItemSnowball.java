package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSnowball extends ProjectileItem {

    public ItemSnowball() {
        this(0, 1);
    }

    public ItemSnowball(Integer meta) {
        this(meta, 1);
    }

    public ItemSnowball(Integer meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    public String getProjectileEntityType() {
        return "Snowball";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
