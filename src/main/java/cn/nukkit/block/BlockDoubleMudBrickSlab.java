package cn.nukkit.block;

public class BlockDoubleMudBrickSlab extends BlockDoubleSlabBase {

    public BlockDoubleMudBrickSlab() {
        this(0);
    }

    public BlockDoubleMudBrickSlab(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MUD_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Double Mud Brick Slab";
    }
    @Override
    public int getSingleSlabId() {
        return MUD_BRICK_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }
}
