package cn.nukkit.block;

public class BlockButtonSpruce extends BlockButtonWooden {
    public BlockButtonSpruce() {
        this(0);
    }
    
    public BlockButtonSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Spruce Button";
    }
}
