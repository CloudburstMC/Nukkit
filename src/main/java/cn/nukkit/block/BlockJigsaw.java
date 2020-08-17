package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

public class BlockJigsaw extends BlockSolidMeta implements Faceable {
    public BlockJigsaw() {
        this(0);
    }
    
    public BlockJigsaw(int meta) {
        super(meta);
    }
    
    @Override
    public String getName() {
        return "Jigsaw";
    }
    
    @Override
    public int getId() {
        return JIGSAW;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public double getResistance() {
        return 18000000;
    }
    
    @Override
    public double getHardness() {
        return -1;
    }
    
    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage());
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            double y = player.y + player.getEyeHeight();
        
            if (y - this.y > 2) {
                this.setDamage(BlockFace.UP.getIndex());
            } else if (this.y - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex());
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
        }
        this.level.setBlock(block, this, true, false);
        
        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
