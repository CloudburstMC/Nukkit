package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockPistonSticky extends BlockPistonBase {

    public BlockPistonSticky() {
        this(0);
    }

    public BlockPistonSticky(int meta) {
        super(meta);
        this.sticky = true;
    }

    @Override
    public int getId() {
        return STICKY_PISTON;
    }

    @Override
    public String getName() {
        return "Sticky Piston";
    }

    @Override
    public int getPistonHeadBlockId() {
        return PISTON_HEAD_STICKY;
    }
}
