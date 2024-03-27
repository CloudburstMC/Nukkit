package cn.nukkit.block;

public class BlockStairsMossyCobblestone extends BlockStairsCobblestone {

    public BlockStairsMossyCobblestone() {
        this(0);
    }

    public BlockStairsMossyCobblestone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mossy Cobblestone Stairs";
    }

    @Override
    public int getId() {
        return MOSSY_COBBLESTONE_STAIRS;
    }
}
