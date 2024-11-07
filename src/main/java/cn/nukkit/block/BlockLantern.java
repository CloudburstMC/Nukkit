package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockLantern extends BlockFlowable {

    public BlockLantern() {
        this(0);
    }

    public BlockLantern(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    @Override
    public int getId() {
        return LANTERN;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(LANTERN));
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.3125;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.3125;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6875;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6875;
    }

    private boolean isBlockAboveValid() {
        Block support = this.up();
        switch (support.getId()) {
            case IRON_BARS:
            case HOPPER_BLOCK:
            case CHAIN_BLOCK:
                return true;
            default:
                if (support instanceof BlockFence || support instanceof BlockGlass || support instanceof BlockGlassPane) {
                    return true;
                }
                if (support instanceof BlockTrapdoor) {
                    return !((BlockTrapdoor) support).isTop() && !((BlockTrapdoor) support).isOpen();
                }
                if (support instanceof BlockSlab && (support.getDamage() & 0x08) == 0x00) {
                    return true;
                }
                if (support instanceof BlockStairs && (support.getDamage() & 0x04) == 0x00) {
                    return true;
                }
                return !support.isTransparent() && support.isSolid() && !support.isPowerSource();
        }
    }

    private boolean isBlockUnderValid() {
        Block down = this.down();
        return !down.isTransparent() || down.isNarrowSurface() || Block.canStayOnFullSolid(down);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean isUnderValid = this.isBlockUnderValid();
        boolean hanging = face != BlockFace.UP && this.isBlockAboveValid() && (!isUnderValid || face == BlockFace.DOWN);
        if (!isUnderValid && !hanging) {
            return false;
        }

        if (hanging) {
            this.setDamage(1);
        } else {
            this.setDamage(0);
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getDamage() == 0) {
                if (!this.isBlockUnderValid()) {
                    level.useBreakOn(this, null, null, true);
                }
            } else if (!this.isBlockAboveValid()) {
                level.useBreakOn(this, null, null, true);
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
