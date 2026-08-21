package cn.nukkit.block;

public class BlockWallTileDeepslate extends BlockWallIndependentID {

    public BlockWallTileDeepslate() {
        this(0);
    }

    public BlockWallTileDeepslate(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Deepslate Tile Wall";
    }

    @Override
    public int getId() {
        return DEEPSLATE_TILE_WALL;
    }
}
