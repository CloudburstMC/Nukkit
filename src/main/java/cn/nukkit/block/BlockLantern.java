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
    public int getId() {
        return LANTERN;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    private boolean isBlockAboveValid() {
        Block up = up();
        if (up instanceof BlockLeaves) {
            return false;
        } else if (up instanceof BlockFence || up instanceof BlockWall) {
            return true;
        } else if (up instanceof BlockSlab) {
            return (up.getDamage() & 0x08) == 0x00;
        } else if (up instanceof BlockStairs) {
            return (up.getDamage() & 0x04) == 0x00;
        } else if (up.isSolid()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isBlockUnderValid() {
        Block down = down();
        if (down instanceof BlockLeaves) {
            return false;
        } else if (down instanceof BlockFence || down instanceof BlockWall) {
            return true;
        } else if (down instanceof BlockSlab) {
            return (down.getDamage() & 0x08) == 0x08;
        } else if (down instanceof BlockStairs) {
            return (down.getDamage() & 0x04) == 0x04;
        } else if (down.isSolid()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if(this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        boolean hanging = false;
        if (face == BlockFace.DOWN) {
            if (isBlockAboveValid()) {
                hanging = true;
            } else if (!isBlockUnderValid()) {
                return false;
            }
        } else if (face == BlockFace.UP && isBlockUnderValid()) {
            hanging = false;
        } else if (isBlockAboveValid()) {
            hanging = true;
        } else {
            return false;
        }

        if (hanging) {
            setDamage(1);
        } else {
            setDamage(0);
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (getDamage() == 0) {
                if (!isBlockUnderValid()) {
                    level.useBreakOn(this);
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
        return y + (getDamage()==0?0: 1./16);
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
        return y + (getDamage()==0? 7.0/16 : 8.0/16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0/16);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLantern());
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

}
