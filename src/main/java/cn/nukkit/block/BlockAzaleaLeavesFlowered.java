package cn.nukkit.block;

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
}
