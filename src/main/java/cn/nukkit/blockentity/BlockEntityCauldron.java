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

    public BlockEntityCauldron(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("PotionId")) {
            namedTag.putShort("PotionId", 0xffff);
        }

        if (!namedTag.contains("SplashPotion")) {
            namedTag.putByte("SplashPotion", 0);
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

    public boolean isSplashPotion() {
        return namedTag.getByte("SplashPotion") > 0;
    }

    public void setSplashPotion(boolean value) {
        namedTag.putByte("SplashPotion", value ? 1 : 0);
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
        return getBlock().getId() == Block.CAULDRON_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.CAULDRON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putByte("SplashPotion", namedTag.getByte("SplashPotion"));
    }
}
