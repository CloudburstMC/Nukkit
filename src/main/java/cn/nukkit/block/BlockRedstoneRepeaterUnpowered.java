package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
public class BlockRedstoneRepeaterUnpowered extends BlockRedstoneRepeater {

    public BlockRedstoneRepeaterUnpowered() {
        super();
        this.isPowered = false;
    }

    @Override
    public int getId() {
        return UNPOWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Unpowered Repeater";
    }

    @Override
    protected Block getPowered() {
        return BlockState.of(BlockID.POWERED_REPEATER, getCurrentState().getDataStorage()).getBlock();
    }

    @Override
    protected Block getUnpowered() {
        return this;
    }
}
