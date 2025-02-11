package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.level.generator.object.tree.ObjectNetherTree;
import cn.nukkit.utils.BlockColor;

public class BlockCrimsonFungus extends BlockFungus {

    public BlockCrimsonFungus() {
    }

    @Override
    public int getId() {
        return CRIMSON_FUNGUS;
    }

    @Override
    public String getName() {
        return "Crimson Fungus";
    }

    @Override
    protected ObjectNetherTree getTree() {
        return new ObjectCrimsonTree();
    }

    @Override
    protected int getGround() {
        return CRIMSON_NYLIUM;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
