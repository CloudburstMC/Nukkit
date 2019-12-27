package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;

public class BlockWoodBark extends BlockWood {
    public BlockWoodBark() {
        this(0);
    }
    
    public BlockWoodBark(int meta) {
        super(MathHelper.clamp(meta, 0, 5));
    }
    
    @Override
    public void setDamage(int meta) {
        super.setDamage(MathHelper.clamp(meta, 0, 5));
    }
    
    @Override
    public int getId() {
        return WOOD_BARK;
    }
    
    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Wood",
                "Spruce Wood",
                "Birch Wood",
                "Jungle Wood",
                "Acacia Wood",
                "Dark Oak Wood",
        };
        return names[getDamage() % names.length];
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(item.getDamage() % 6);
        this.getLevel().setBlock(block, this, true, true);
    
        return true;
    }
    
    @Override
    public Item toItem() {
        return new ItemBlock(new BlockWoodBark(getDamage()));
    }
}
