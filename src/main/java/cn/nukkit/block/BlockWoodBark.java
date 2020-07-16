package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;

public class BlockWoodBark extends BlockWood {
    public BlockWoodBark() {
        this(0);
    }
    
    public BlockWoodBark(int meta) {
        super(meta);
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
                // illegal
                "Oak Wood",
                "Oak Wood"
        };
        return names[getDamage() & 0x7];
    }
    
    @Override
    protected int getStrippedId() {
        return getId();
    }
    
    @Override
    protected int getStrippedDamage() {
        return getDamage() | 0x8;
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face.getAxis().isHorizontal()) {
            if (face.getAxis() == BlockFace.Axis.X) {
                setDamage(getDamage() | 0x10);
            } else {
                setDamage(getDamage() | 0x20);
            }
        }
        this.getLevel().setBlock(block, this, true, true);
        
        return true;
    }
    
    @Override
    public Item toItem() {
        return new ItemBlock(new BlockWoodBark(), getDamage() & 0xF);
    }
}
