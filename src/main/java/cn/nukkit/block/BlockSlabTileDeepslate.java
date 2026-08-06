package cn.nukkit.block;

public class BlockSlabTileDeepslate extends BlockSlabDeepslate {

    public BlockSlabTileDeepslate() {
        this(0);
    }

    public BlockSlabTileDeepslate(int meta) {
        super(meta, DEEPSLATE_TILE_SLAB);
    }

    @Override
    public int getId() {
        return DEEPSLATE_TILE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Deepslate Tile";
    }
}
