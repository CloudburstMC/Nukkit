package cn.nukkit.block;

public class BlockSlabBrickDeepslate extends BlockSlabDeepslate {

    public BlockSlabBrickDeepslate() {
        this(0);
    }

    public BlockSlabBrickDeepslate(int meta) {
        super(meta, DEEPSLATE_BRICK_SLAB);
    }

    @Override
    public int getId() {
        return DEEPSLATE_BRICK_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Deepslate Brick";
    }
}
