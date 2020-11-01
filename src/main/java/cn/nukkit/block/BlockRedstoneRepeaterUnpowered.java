package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstoneRepeater;
import cn.nukkit.math.BlockFace;

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
        return (1 + getPropertyValue(REPEATER_DELAY)) * 2;
    }

    @Override
    protected Block getPowered() {
        return BlockState.of(BlockID.POWERED_REPEATER, getCurrentState().getDataStorage()).getBlock();
    }

    @Override
    protected Block getUnpowered() {
        return this;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }

}
