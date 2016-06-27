package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSlab extends BlockTransparent {
    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;


    public BlockSlab() {
        this(0);
    }

    public BlockSlab(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SLAB;
    }

    //todo hardness and residence

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone",
                "Sandstone",
                "Wooden",
                "Cobblestone",
                "Brick",
                "Stone Brick",
                "Quartz",
                "Nether Brick"
        };

        return ((this.meta & 0x08) > 0 ? "Upper " : "") + names[this.meta & 0x07] + " Slab";
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
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.meta &= 0x07;
        if (face == 0) {
            if (target.getId() == SLAB && (target.getDamage() & 0x08) == 0x08 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, new BlockDoubleSlab(this.meta), true);

                return true;
            } else if (block.getId() == SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, new BlockDoubleSlab(this.meta), true);

                return true;
            } else {
                this.meta |= 0x08;
            }
        } else if (face == 1) {
            if (target.getId() == SLAB && (target.getDamage() & 0x08) == 0 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, new BlockDoubleSlab(this.meta), true);

                return true;
            } else if (block.getId() == SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, new BlockDoubleSlab(this.meta), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block.getId() == SLAB) {
                if ((block.getDamage() & 0x07) == (this.meta & 0x07)) {
                    this.getLevel().setBlock(block, new BlockDoubleSlab(this.meta), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    this.meta |= 0x08;
                }
            }
        }

        if (block.getId() == SLAB && (target.getDamage() & 0x07) != (this.meta & 0x07)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {this.getId(), this.meta & 0x07, 1}
            };
        } else {
            return new int[0][0];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        switch (this.meta & 0x07) {
            case BlockSlab.STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockSlab.SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case BlockSlab.WOODEN:
                return BlockColor.WOOD_BLOCK_COLOR;
            case BlockSlab.COBBLESTONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockSlab.BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockSlab.STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockSlab.QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case BlockSlab.NETHER_BRICK:
                return BlockColor.NETHERRACK_BLOCK_COLOR;

            default:
                return BlockColor.STONE_BLOCK_COLOR;     //unreachable
        }
    }
}
