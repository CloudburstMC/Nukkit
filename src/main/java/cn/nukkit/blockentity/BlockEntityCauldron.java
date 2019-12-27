package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import cn.nukkit.utils.BlockColor;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class BlockEntityCauldron extends BlockEntitySpawnable {
    
    public static final int POTION_TYPE_EMPTY = 0xFFFF;
    public static final int POTION_TYPE_NORMAL = 0;
    public static final int POTION_TYPE_SPLASH = 1;
    public static final int POTION_TYPE_LINGERING = 2;
    public static final int POTION_TYPE_LAVA = 0xF19B;

    public BlockEntityCauldron(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        int potionId;
        if (!namedTag.contains("PotionId")) {
            namedTag.putShort("PotionId", 0xffff);
        }
        potionId = namedTag.getShort("PotionId");
        
        int potionType = (potionId & 0xFFFF) == 0xFFFF? POTION_TYPE_EMPTY : POTION_TYPE_NORMAL;
        if (namedTag.getBoolean("SplashPotion")) {
            potionType = POTION_TYPE_SPLASH;
            namedTag.remove("SplashPotion");
        }
        
        if (!namedTag.contains("PotionType")) {
            namedTag.putShort("PotionType", potionType);
        }


        super.initBlockEntity();
    }

    public int getPotionId() {
        return namedTag.getShort("PotionId");
    }

    public void setPotionId(int potionId) {
        namedTag.putShort("PotionId", potionId);
        this.spawnToAll();
    }

    public boolean hasPotion() {
        return getPotionId() != 0xffff;
    }
    
    public void setPotionType(int potionType) {
        this.namedTag.putShort("PotionType", potionType & 0xFFFF);
    }
    
    public int getPotionType() {
        return this.namedTag.getShort("PotionType") & 0xFFFF;
    }

    public boolean isSplashPotion() {
        return namedTag.getShort("PotionType") == POTION_TYPE_SPLASH;
    }
    
    /**
     * @deprecated Use {@link #setPotionType(int)} instead.
     */
    @Deprecated
    public void setSplashPotion(boolean value) {
        namedTag.putShort("PotionType", value ? 1 : 0);
    }

    public BlockColor getCustomColor() {
        if (isCustomColor()) {
            int color = namedTag.getInt("CustomColor");

            int red = (color >> 16) & 0xff;
            int green = (color >> 8) & 0xff;
            int blue = (color) & 0xff;

            return new BlockColor(red, green, blue);
        }

        return null;
    }

    public boolean isCustomColor() {
        return namedTag.contains("CustomColor");
    }

    public void setCustomColor(BlockColor color) {
        setCustomColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setCustomColor(int r, int g, int b) {
        int color = (r << 16 | g << 8 | b) & 0xffffff;

        namedTag.putInt("CustomColor", color);
        spawnToAll();
    }

    public void clearCustomColor() {
        namedTag.remove("CustomColor");
        spawnToAll();
    }

    @Override
    public boolean isBlockEntityValid() {
        int id = getBlock().getId();
        return id == Block.CAULDRON_BLOCK || id == Block.LAVA_CAULDRON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.CAULDRON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putByte("PotionType", namedTag.getShort("PotionType"));
    }
}
