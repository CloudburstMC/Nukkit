package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockAzaleaLeavesFlowered extends BlockAzaleaLeaves {

    public BlockAzaleaLeavesFlowered() {
        this(0);
    }

    public BlockAzaleaLeavesFlowered(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Flowering Azalea Leaves";
    }

    @Override
    public int getId() {
        return AZALEA_LEAVES_FLOWERED;
    }

    @Override
    protected Item getSapling() {
        return new ItemBlock(Block.get(Block.FLOWERING_AZALEA, 0));
    }
}
