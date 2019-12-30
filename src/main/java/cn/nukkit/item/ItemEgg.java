package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemEgg extends ProjectileItem {

    public ItemEgg(Identifier id) {
        super(id);
    }

    @Override
    public String getProjectileEntityType() {
        return "Egg";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    public int getMaxStackSize() { return 16; }
}
