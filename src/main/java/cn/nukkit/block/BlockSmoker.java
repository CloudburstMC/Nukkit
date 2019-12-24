package cn.nukkit.block;

public class BlockSmoker extends BlockSmokerBurning {
    public BlockSmoker() {
        this(0);
    }

    public BlockSmoker(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Smoker";
    }

    @Override
    public int getId() {
        return SMOKER;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
