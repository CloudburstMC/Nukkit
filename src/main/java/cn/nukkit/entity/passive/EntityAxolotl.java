package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityAxolotl extends EntityAnimal {

    public static final int NETWORK_ID = 130;

    public EntityAxolotl(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(14);
    }

    @Override
    public String getName() {
        return "Axolotl";
    }
}
