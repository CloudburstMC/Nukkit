package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
public class BlockRedstoneRepeaterPowered extends BlockRedstoneRepeater {

    public BlockRedstoneRepeaterPowered() {
        super();
        this.isPowered = true;
    }

    @Override
    public int getId() {
        return POWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Powered Repeater";
    }

    @Override
    protected Block getPowered() {
        return this;
    }

    @Override
    protected Block getUnpowered() {
        return BlockState.of(BlockID.UNPOWERED_REPEATER, getCurrentState().getDataStorage()).getBlock();
    }

    @Override
    public int getLightLevel() {
        return 7;
    }
}
