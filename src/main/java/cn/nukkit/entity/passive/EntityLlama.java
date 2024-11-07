package cn.nukkit.entity.passive;

import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityLlama extends EntityHorseBase {

    public static final int NETWORK_ID = 29;

    private int variant;

    private static final int[] VARIANTS = {0, 1, 2, 3};

    public EntityLlama(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.935f;
        }
        return 1.87f;
    }


    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();

        if (this.namedTag.contains("Variant")) {
            this.variant = this.namedTag.getInt("Variant");
        } else {
            this.variant = getRandomVariant();
        }

        this.setDataProperty(new IntEntityData(DATA_VARIANT, this.variant));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("Variant", this.variant);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER, 0, Utils.rand(0, 2))};
    }

    private static int getRandomVariant() {
        return VARIANTS[Utils.rand(0, VARIANTS.length - 1)];
    }
}
