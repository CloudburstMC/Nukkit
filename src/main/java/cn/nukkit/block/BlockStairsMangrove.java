package cn.nukkit.block;

public class BlockStairsMangrove extends BlockStairsWood {

    public BlockStairsMangrove() {
        this(0);
    }

    public BlockStairsMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Stairs";
    }

    @Override
    public int getId() {
        return MANGROVE_STAIRS;
    }
}
