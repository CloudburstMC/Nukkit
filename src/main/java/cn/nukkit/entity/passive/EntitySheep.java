package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockWood;
import cn.nukkit.block.BlockWool;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntitySheep extends EntityAnimal {

    public static final int NETWORK_ID = 13;

    public boolean sheared = false;
    public int color = 0;

    public EntitySheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.9f; // No have information
        }
        return 1.3f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.95f * 0.9f; // No have information
        }
        return 0.95f * getHeight();
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);

        if (!this.namedTag.contains("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean onInteract(Entity entity, Item item) {
        if (item.getId() == Item.DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());

            if (entity instanceof EntityHumanType) {
                EntityHumanType human = (EntityHumanType) entity;
                item.setCount(item.getCount() - 1);

                if (item.getCount() <= 0) {
                    human.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else {
                    human.getInventory().setItemInHand(item);
                }
            }
            return true;
        }

        if (item.getId() != Item.SHEARS) {
            return false;
        }

        if (shear()) {
            if (entity instanceof EntityHumanType) {
                EntityHumanType human = (EntityHumanType) entity;
                item.setDamage(item.getDamage() + 1);

                if (item.getDamage() >= item.getMaxDurability()) {
                    human.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else {
                    human.getInventory().setItemInHand(item);
                }
            }

            return true;
        }

        return false;
    }

    public boolean shear() {
        if (sheared) {
            return false;
        }

        this.sheared = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true);

        this.level.dropItem(this, new ItemBlock(new BlockWool(this.getColor()), 0, this.level.rand.nextInt(2) + 1));
        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(Item.WOOL, getColor(), 1)};
        }
        return new Item[0];
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }
}