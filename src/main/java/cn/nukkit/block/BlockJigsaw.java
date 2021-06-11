package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

@PowerNukkitOnly
public class BlockJigsaw extends BlockSolidMeta implements Faceable {
    private static final IntBlockProperty ROTATION = new IntBlockProperty("rotation", false, 3);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, ROTATION);

    @PowerNukkitOnly
    public BlockJigsaw() {
        this(0);
    }

    @PowerNukkitOnly
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
