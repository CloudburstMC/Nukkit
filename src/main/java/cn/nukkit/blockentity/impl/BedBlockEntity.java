package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.Bed;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.DyeColor;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BedBlockEntity extends BaseBlockEntity implements Bed {

    public DyeColor color = DyeColor.WHITE;

    public BedBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForByte("color", this::setColor);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.byteTag("color", (byte) this.getColor().getDyeData());
    }

    @Override
    public boolean isValid() {
        return this.getLevel().getBlockId(this.getPosition()) == BlockIds.BED;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    private void setColor(int color) {
        this.color = DyeColor.getByDyeData(color);
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
