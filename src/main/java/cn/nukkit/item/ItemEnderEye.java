package cn.nukkit.item;

public class ItemEnderEye extends ProjectileItem {

    public ItemEnderEye() {
        this(0, 1);
    }

    public ItemEnderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderEye(Integer meta, int count) {
        super(ENDER_EYE, meta, count, "Ender Eye");
    }

    @Override
    public String getProjectileEntityType() {
        return "EnderEye";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
