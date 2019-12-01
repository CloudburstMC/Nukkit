package cn.nukkit.entity.passive;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Random;

/**
 * @author Erik Miller
 * @version 1.0
 */
public class EntitySnowMan extends EntityMob {

    public static final int NETWORK_ID = 21;

    public EntitySnowMan(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(4);
    }


    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SNOWBALL, 0, new Random().nextInt( 15 ))};
    }

    @Override
    public String getName() {
        return "Snow Golem";
    }



}
