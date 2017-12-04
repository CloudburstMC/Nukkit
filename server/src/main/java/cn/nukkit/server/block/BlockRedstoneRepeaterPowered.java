package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemRedstoneRepeater;
import cn.nukkit.server.math.BlockFace;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneRepeaterPowered extends BlockRedstoneDiode {

    public BlockRedstoneRepeaterPowered() {
        this(0);
    }

    public BlockRedstoneRepeaterPowered(int meta) {
        super(meta);
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
        return this;
    }

    @Override
    protected Block getUnpowered() {
        return new BlockRedstoneRepeaterUnpowered(this.meta);
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.meta += 4;
        if (this.meta > 15) this.meta = this.meta % 4;

        this.level.setBlock(this, this, true, false);
        return true;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }
}
