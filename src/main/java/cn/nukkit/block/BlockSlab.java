package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    protected final int doubleSlab;

    public BlockSlab(int meta, int doubleSlab) {
        super(meta);
        this.doubleSlab = doubleSlab;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if ((this.meta & 0x08) > 0) {
            return new AxisAlignedBB(
                    this.x,
                    this.y + 0.5,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else {
            return new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + 0.5,
                    this.z + 1
            );
        }
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
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.meta &= 0x07;
        if (face == 0) {
            if (target instanceof BlockSlab && (target.getDamage() & 0x08) == 0x08 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.meta), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.meta), true);

                return true;
            } else {
                this.meta |= 0x08;
            }
        } else if (face == 1) {
            if (target instanceof BlockSlab && (target.getDamage() & 0x08) == 0 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.meta), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.meta), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if ((block.getDamage() & 0x07) == (this.meta & 0x07)) {
                    this.getLevel().setBlock(block, Block.get(doubleSlab, this.meta), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    this.meta |= 0x08;
                }
            }
        }

        if (block instanceof BlockSlab && (target.getDamage() & 0x07) != (this.meta & 0x07)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
}