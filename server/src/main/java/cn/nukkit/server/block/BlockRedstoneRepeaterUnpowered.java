package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemRedstoneRepeater;
import cn.nukkit.server.math.BlockFace;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneRepeaterUnpowered extends BlockRedstoneDiode {

    public BlockRedstoneRepeaterUnpowered() {
        this(0);
    }

    public BlockRedstoneRepeaterUnpowered(int meta) {
        super(meta);
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
    public boolean onActivate(Item item, Player player) {
        this.meta += 4;
        if (this.meta > 15) this.meta = this.meta % 4;

        this.level.setBlock(this, this, true, false);
        return true;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(meta);
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
        return (1 + (meta >> 2)) * 2;
    }

    @Override
    protected Block getPowered() {
        return new BlockRedstoneRepeaterPowered(this.meta);
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