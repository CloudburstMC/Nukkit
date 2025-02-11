package cn.nukkit.block;

public class BlockMangrovePlanks extends BlockPlanks {

    public BlockMangrovePlanks() {
        this(0);
    }

    public BlockMangrovePlanks(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Planks";
    }

    @Override
    public int getId() {
        return MANGROVE_PLANKS;
    }
}
