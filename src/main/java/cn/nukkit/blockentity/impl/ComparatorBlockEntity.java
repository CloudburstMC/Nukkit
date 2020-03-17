package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Comparator;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class ComparatorBlockEntity extends BaseBlockEntity implements Comparator {

    private int outputSignal;

    public ComparatorBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("OutputSignal", this::setOutputSignal);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.intTag("OutputSignal", this.getOutputSignal());
    }

    @Override
    public boolean isValid() {
        return this.getBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
