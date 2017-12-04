package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemEgg extends ProjectileItem {

    public ItemEgg() {
        this(0, 1);
    }

    public ItemEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemEgg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }

    @Override
    public String getProjectileEntityType() {
        return "Egg";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
