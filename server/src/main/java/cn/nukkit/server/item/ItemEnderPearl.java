package cn.nukkit.server.item;

public class ItemEnderPearl extends ProjectileItem {

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public String getProjectileEntityType() {
        return "EnderPearl";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
