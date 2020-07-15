package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;

public class BlockCrimsonStrippedHyphae extends BlockSolid {
    
    public BlockCrimsonStrippedHyphae() {
    }
    
    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_CRIMSON_HYPHAE;
    }
    
    @Override
    public String getName() {
        return "Crimson Stripped Hyphae";
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
        return new ItemBlock(this);
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
