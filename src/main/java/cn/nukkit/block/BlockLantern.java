package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockLantern extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(HANGING);

    @PowerNukkitOnly
    public BlockLantern() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockLantern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LANTERN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    private boolean isBlockAboveValid() {
        Block support = up();
        switch (support.getId()) {
            case CHAIN_BLOCK:
            case IRON_BARS:
            case HOPPER_BLOCK:
                return true;
            default:
                if (support instanceof BlockWallBase || support instanceof BlockFence) {
                    return true;
                }
                if (support instanceof BlockSlab && !((BlockSlab) support).isOnTop()) {
                    return true;
                }
                if (support instanceof BlockStairs && !((BlockStairs) support).isUpsideDown()) {
                    return true;
                }
                return BlockLever.isSupportValid(support, BlockFace.DOWN);
        }
    }

    private boolean isBlockUnderValid() {
        Block support = down();
        if (support.getId() == HOPPER_BLOCK) {
            return true;
        }
        if (support instanceof BlockWallBase || support instanceof BlockFence) {
            return true;
        }
        return BlockLever.isSupportValid(support, BlockFace.UP);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if(this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        boolean hanging = face != BlockFace.UP && isBlockAboveValid() && (!isBlockUnderValid() || face == BlockFace.DOWN);
        if (!isBlockUnderValid() && !hanging) {
            return false;
        }
        
        setHanging(hanging);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isHanging()) {
                if (!isBlockUnderValid()) {
                    level.useBreakOn(this, ItemTool.getBestTool(getToolType()));
                }
            } else if (!isBlockAboveValid()) {
                level.useBreakOn(this);
            }
            return type;
        }
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public double getHardness() {
        return 5.0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return x + (5.0/16);
    }

    @Override
    public double getMinY() {
        return y + (!isHanging()?0: 1./16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0/16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0/16);
    }

    @Override
    public double getMaxY() {
        return y + (!isHanging()? 7.0/16 : 8.0/16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0/16);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isHanging() {
        return getBooleanValue(HANGING);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setHanging(boolean hanging) {
        setBooleanValue(HANGING, hanging);
    }
}
