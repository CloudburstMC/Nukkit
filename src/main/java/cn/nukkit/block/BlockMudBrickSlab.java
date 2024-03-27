package cn.nukkit.block;

public class BlockMudBrickSlab extends BlockSlab {

    public BlockMudBrickSlab() {
        this(0);
    }

    public BlockMudBrickSlab(int meta) {
        super(meta, MUD_BRICK_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return MUD_BRICK_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Mud Brick Slab";
    }
}
