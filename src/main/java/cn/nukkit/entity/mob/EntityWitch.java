package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EntityWitch extends EntityWalkingMob {

    public static final int NETWORK_ID = 45;

    public EntityWitch(FullChunk chunk, CompoundTag nbt) {
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
        this.setMaxHealth(26);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        drops.add(Item.get(Item.REDSTONE, 0, Utils.rand(4, 8)));

        if (Utils.rand(1, 4) == 1) {
            drops.add(Item.get(Item.STICK, 0, Utils.rand(0, 2)));
        }

        if (Utils.rand(1, 3) == 1) {
            switch (Utils.rand(1, 5)) {
                case 1:
                    drops.add(Item.get(Item.BOTTLE, 0, Utils.rand(0, 2)));
                    break;
                case 2:
                    drops.add(Item.get(Item.GLOWSTONE_DUST, 0, Utils.rand(0, 2)));
                    break;
                case 3:
                    drops.add(Item.get(Item.GUNPOWDER, 0, Utils.rand(0, 2)));
                    break;
                case 4:
                    drops.add(Item.get(Item.SPIDER_EYE, 0, Utils.rand(0, 2)));
                    break;
                case 5:
                    drops.add(Item.get(Item.SUGAR, 0, Utils.rand(0, 2)));
                    break;
            }
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }
}
