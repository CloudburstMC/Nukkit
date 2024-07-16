package cn.nukkit.block;

public class BlockWallDeepslateCobbled extends BlockWall {

    public BlockWallDeepslateCobbled() {
        this(0);
    }

    public BlockWallDeepslateCobbled(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cobbled Deepslate Wall";
    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_WALL;
    }
}
