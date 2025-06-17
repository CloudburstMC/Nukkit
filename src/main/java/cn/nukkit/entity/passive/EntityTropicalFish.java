package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityTropicalFish extends EntityFish {

    public static final int NETWORK_ID = 111;

    private int variantA;
    private int variantB;
    private int color;

    public EntityTropicalFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();

        if (this.namedTag.contains("VariantA")) {
            this.variantA = this.namedTag.getInt("VariantA");
        } else {
            this.variantA = Utils.rand(0, 5);

        }
        this.dataProperties.putInt(DATA_VARIANT, this.variantA);

        if (this.namedTag.contains("VariantB")) {
            this.variantB = this.namedTag.getInt("VariantB");
        } else {
            this.variantB = Utils.rand(0, 5);

        }
        this.dataProperties.putInt(DATA_MARK_VARIANT, this.variantB);

        if (this.namedTag.contains("Color")) {
            this.color = this.namedTag.getInt("Color");
        } else {
            this.color = Utils.rand(0, 15);

        }
        this.dataProperties.putByte(DATA_COLOR, this.color);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("VariantA", this.variantA);
        this.namedTag.putInt("VariantB", this.variantB);
        this.namedTag.putInt("Color", this.color);
    }

    @Override
    protected int getBucketMeta() {
        return 4;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.CLOWNFISH, 0, 1), Item.get(Item.BONE, 0, Utils.rand(0, 2))};
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Tropical Fish";
    }
}
