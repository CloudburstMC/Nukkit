package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

public class BlockCampfire extends BlockSolid implements Faceable {

    private static final int CAMPFIRE_LIT_MASK = 0x08;
    private static final int CAMPFIRE_FACING_MASK = 0x07;

    public BlockCampfire(Identifier id) {
        super(id);
    }

    public BlockCampfire(Identifier id, int meta) {
        super(id);
        this.setDamage(meta);
    }

    @Override
    public double getHardness() {
        return 2.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & CAMPFIRE_FACING_MASK);
    }

    public boolean isLit() {
        return (this.getDamage() & CAMPFIRE_LIT_MASK) > 0;
    }
}
