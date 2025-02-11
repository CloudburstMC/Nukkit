package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EntityZombiePigman extends EntityWalkingMob implements EntitySmite {

    public static final int NETWORK_ID = 36;

    public EntityZombiePigman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        this.fireProof = true;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (!this.isBaby()) {
            drops.add(Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 1)));
            drops.add(Item.get(Item.GOLD_NUGGET, 0, Utils.rand(0, 1)));

            for (int i = 0; i < (Utils.rand(0, 101) <= 9 ? 1 : 0); i++) {
                drops.add(Item.get(Item.GOLD_SWORD, Utils.rand(20, 30), 1));
            }
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : 5;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Zombified Piglin";
    }
}
