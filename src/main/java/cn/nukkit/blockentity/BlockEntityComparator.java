package cn.nukkit.blockentity;

import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 * @author nmaster
 */
public class BlockEntityComparator extends BlockEntity {

    public BlockEntityComparator(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!nbt.contains("OutputSignal")) {
            nbt.putInt("OutputSignal", 0);
        }

    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return this.namedTag.getInt("OutputSignal");
    }

    public void setOutputSignal(int outputSignal) {
        this.namedTag.putInt("OutputSignal", outputSignal);
    }

}
