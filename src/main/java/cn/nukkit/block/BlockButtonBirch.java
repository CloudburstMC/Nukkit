package cn.nukkit.block;

public class BlockButtonBirch extends BlockButtonWooden {
    public BlockButtonBirch() {
        this(0);
    }
    
    public BlockButtonBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BIRCH_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Birch Button";
    }
}
