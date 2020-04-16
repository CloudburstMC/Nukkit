package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockDaylightDetector;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.DaylightDetector;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

public class DaylightDetectorBlockEntity extends BaseBlockEntity implements DaylightDetector {

    private int redstoneSignal;

    public DaylightDetectorBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        Identifier blockId = getBlock().getId();
        return blockId == BlockIds.DAYLIGHT_DETECTOR || blockId == BlockIds.DAYLIGHT_DETECTOR_INVERTED;
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("redstone_signal", this::setRedstoneSignal);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.intTag("redstone_signal", this.getRedstoneSignal());
    }

    public int getRedstoneSignal() {
        return redstoneSignal;
    }

    public void setRedstoneSignal(int redstoneSignal) {
        this.redstoneSignal = redstoneSignal;
    }
}
