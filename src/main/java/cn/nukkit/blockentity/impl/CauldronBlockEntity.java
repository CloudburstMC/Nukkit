package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Cauldron;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.BlockColor;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class CauldronBlockEntity extends BaseBlockEntity implements Cauldron {

    private short potionType;
    private short potionId;
    private boolean splash;
    private BlockColor customColor = BlockColor.WHITE_BLOCK_COLOR;

    public CauldronBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        // TODO: "Items" tag 0-10 slots
        tag.listenForShort("PotionType", this::setPotionType);
        tag.listenForShort("PotionId", this::setPotionId);
        tag.listenForBoolean("IsSplash", this::setSplash);
        tag.listenForInt("CustomColor", this::setCustomColor);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.shortTag("PotionType", this.getPotionType());
        tag.shortTag("PotionId", this.getPotionId());
        tag.booleanTag("IsSplash", this.isSplash());
        if (this.customColor != null) {
            tag.intTag("CustomColor", this.customColor.getRGB());
        }
    }

    @Override
    public short getPotionId() {
        return potionId;
    }

    @Override
    public void setPotionId(int potionId) {
        this.potionId = (short) potionId;
    }

    @Override
    public short getPotionType() {
        return potionType;
    }

    @Override
    public void setPotionType(int potionType) {
        this.potionType = (short) potionType;
    }

    @Override
    public boolean isSplash() {
        return splash;
    }

    @Override
    public void setSplash(boolean splash) {
        this.splash = splash;
    }

    @Override
    public BlockColor getCustomColor() {
        return this.customColor;
    }

    @Override
    public void setCustomColor(BlockColor customColor) {
        this.customColor = customColor;
    }

    private void setCustomColor(int customColor) {
        this.customColor = new BlockColor(customColor);
    }

    @Override
    public boolean hasCustomColor() {
        return this.customColor != null;
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.CAULDRON;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
