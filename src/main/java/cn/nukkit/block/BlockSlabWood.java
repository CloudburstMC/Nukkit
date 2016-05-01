package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSlabWood extends BlockSlab {

    public BlockSlabWood() {
        this(0);
    }

    public BlockSlabWood(int meta) {
        super(meta);
    }


    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak",
                "Spruce",
                "Birch",
                "Jungle",
                "Acacia",
                "Dark Oak",
                "",
                ""
        };
        return (((this.meta & 0x08) == 0x08) ? "Upper " : "") + names[this.meta & 0x07] + " Wooden Slab";
    }

    @Override
    public int getId() {
        return WOOD_SLAB;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.meta &= 0x07;
        if (face == 0) {
            if (target.getId() == WOOD_SLAB && (target.getDamage() & 0x08) == 0x08 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, new BlockDoubleSlabWood(this.meta), true);

                return true;
            } else if (block.getId() == WOOD_SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, new BlockDoubleSlabWood(this.meta), true);

                return true;
            } else {
                this.meta |= 0x08;
            }
        } else if (face == 1) {
            if (target.getId() == WOOD_SLAB && (target.getDamage() & 0x08) == 0 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, new BlockDoubleSlabWood(this.meta), true);

                return true;
            } else if (block.getId() == WOOD_SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, new BlockDoubleSlabWood(this.meta), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block.getId() == WOOD_SLAB) {
                if ((block.getDamage() & 0x07) == (this.meta & 0x07)) {
                    this.getLevel().setBlock(block, new BlockDoubleSlabWood(this.meta), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    this.meta |= 0x08;
                }
            }
        }

        if (block.getId() == WOOD_SLAB && (target.getDamage() & 0x07) != (this.meta & 0x07)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), this.meta & 0x07, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
