package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemEnderPearl extends ProjectileItem {

    public ItemEnderPearl(Identifier id) {
        super(id);
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
