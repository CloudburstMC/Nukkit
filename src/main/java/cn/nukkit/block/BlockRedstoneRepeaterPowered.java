package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstoneRepeater;
import cn.nukkit.math.BlockFace;

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
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(getDamage());
    }

    @Override
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem() {
        return new ItemRedstoneRepeater();
    }

    @Override
    protected int getDelay() {
        return (1 + (getDamage() >> 2)) * 2;
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

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}
