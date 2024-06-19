package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.BlockColor;

import java.util.Collection;

/**
 * @author CreeperFace
 * Nukkit Project
 */
public class BlockEntityCauldron extends BlockEntitySpawnable {

    public static final int POTION_TYPE_EMPTY = 0xFFFF;
    public static final int POTION_TYPE_NORMAL = 0;
    public static final int POTION_TYPE_SPLASH = 1;
    public static final int POTION_TYPE_LINGERING = 2;

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
        setDirty();
        this.spawnToAll();
    }

    public boolean hasPotion() {
        return getPotionId() != 0xffff;
    }

    public void setPotionType(int potionType) {
        this.namedTag.putShort("PotionType", potionType & 0xFFFF);
        setDirty();
    }

    public int getPotionType() {
        return this.namedTag.getShort("PotionType") & 0xFFFF;
    }

    public boolean isSplashPotion() {
        return namedTag.getShort("PotionType") == POTION_TYPE_SPLASH;
    }

    public void setSplashPotion(boolean value) {
        namedTag.putShort("PotionType", value ? 1 : 0);
        setDirty();
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

        if (color != namedTag.getInt("CustomColor")) {
            namedTag.putInt("CustomColor", color);
            Block block = getBlock();
            Collection<Player> pl = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
            for (Player p : pl) {
                UpdateBlockPacket air = new UpdateBlockPacket();
                air.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(0);
                air.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
                air.x = (int) x;
                air.y = (int) y;
                air.z = (int) z;
                UpdateBlockPacket self = (UpdateBlockPacket) air.clone();
                self.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
                p.dataPacket(air);
                p.dataPacket(self);
            }

            setDirty();

            spawnToAll();
        }
    }

    public void clearCustomColor() {
        namedTag.remove("CustomColor");
        setDirty();
        spawnToAll();
    }

    @Override
    public boolean isBlockEntityValid() {
        int id = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return id == Block.CAULDRON_BLOCK || id == Block.LAVA_CAULDRON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag compoundTag = new CompoundTag()
                .putString("id", BlockEntity.CAULDRON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putByte("PotionType", namedTag.getShort("PotionType"));
        if (namedTag.contains("CustomColor")) {
            compoundTag.putInt("CustomColor", namedTag.getInt("CustomColor"));
        }
        return compoundTag;
    }
}
