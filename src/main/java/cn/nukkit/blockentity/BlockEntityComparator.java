package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;

    public BlockEntityComparator(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!nbt.contains("OutputSignal")) {
            nbt.putInt("OutputSignal", 0);
        }

        this.outputSignal = nbt.getInt("OutputSignal");
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return blockID == Block.POWERED_COMPARATOR || blockID == Block.UNPOWERED_COMPARATOR;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        if (this.outputSignal != outputSignal) {
            this.outputSignal = outputSignal;
            setDirty();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("OutputSignal", this.outputSignal);
    }
}
