package cn.nukkit.block;

public class BlockPumpkinCarved extends BlockPumpkin {

    public BlockPumpkinCarved() {
    }

    public BlockPumpkinCarved(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Carved Pumpkin";
    }

    @Override
    public int getId() {
        return CARVED_PUMPKIN;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }
}
