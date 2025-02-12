package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectNetherTree;
import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.utils.BlockColor;

public class BlockWarpedFungus extends BlockFungus {

    public BlockWarpedFungus() {
    }

    @Override
    public int getId() {
        return WARPED_FUNGUS;
    }

    @Override
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    protected ObjectNetherTree getTree() {
        return new ObjectWarpedTree();
    }

    @Override
    protected int getGround() {
        return WARPED_NYLIUM;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
