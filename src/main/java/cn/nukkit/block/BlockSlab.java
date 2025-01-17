package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparentMeta {

    protected final int doubleSlab;

    public BlockSlab(int meta, int doubleSlab) {
        super(meta);
        this.doubleSlab = doubleSlab;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (this.hasTopBit()) {
            return new SimpleAxisAlignedBB(
                    this.x,
                    this.y + 0.5,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else {
            return new SimpleAxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + 0.5,
                    this.z + 1
            );
        }
    }

    public String getSlabName() {
        return "";
    }

    @Override
    public String getName() {
        return (this.hasTopBit()? "Upper " : "") + this.getSlabName() + " Slab";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return getToolType() < ItemTool.TYPE_AXE ? 30 : 15;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(this.getDamage() & 0x07);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && ((BlockSlab) target).doubleSlab == this.doubleSlab && ((BlockSlab) target).hasTopBit() && (target.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && ((BlockSlab) block).doubleSlab == this.doubleSlab && (block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else {
                this.setTopBit(true);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && ((BlockSlab) target).doubleSlab == this.doubleSlab && !((BlockSlab) target).hasTopBit() && (target.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && ((BlockSlab) block).doubleSlab == this.doubleSlab && (block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab && ((BlockSlab) block).doubleSlab == this.doubleSlab) {
                if ((block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                    this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    this.setTopBit(true);
                }
            }
        }

        if (block instanceof BlockSlab && ((BlockSlab) block).doubleSlab == this.doubleSlab && (target.getDamage() & 0x07) != (this.getDamage() & 0x07)) {
            return false;
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    public boolean hasTopBit() {
        return (this.getDamage() & 0x08) > 0;
    }

    public void setTopBit(boolean topBit) {
        if (topBit) {
            this.setDamage(this.getDamage() | 0x08);
        } else {
            this.setDamage(this.getDamage() & 0x07);
        }
    }

    @Override
    public Item toItem() {
        int damage = this.getDamage() & 0x07;
        return new ItemBlock(Block.get(this.getId(), damage), damage);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
