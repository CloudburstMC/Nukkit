package cn.nukkit.block;

public class BlockStairsGranitePolished extends BlockStairsGranite {

    public BlockStairsGranitePolished() {
        this(0);
    }

    public BlockStairsGranitePolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Granite Stairs";
    }

    @Override
    public int getId() {
        return POLISHED_GRANITE_STAIRS;
    }
}
