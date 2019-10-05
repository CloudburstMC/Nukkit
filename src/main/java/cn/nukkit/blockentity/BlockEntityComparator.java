package cn.nukkit.blockentity;

import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;

    public BlockEntityComparator(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!nbt.contains("OutputSignal")) {
            nbt.putInt("OutputSignal", 0);
        }

        this.outputSignal = nbt.getInt("OutputSignal");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("OutputSignal", this.outputSignal);
    }
}
