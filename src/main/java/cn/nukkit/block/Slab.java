package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Slab extends Transparent {
    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;


    public Slab() {
        this(0);
    }

    public Slab(int meta) {
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
                this.getLevel().setBlock(target, Block.get(Item.DOUBLE_SLAB, this.meta), true);

                return true;
            } else if (block.getId() == SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, Block.get(Item.DOUBLE_SLAB, this.meta), true);

                return true;
            } else {
                this.meta |= 0x08;
            }
        } else if (face == 1) {
            if (target.getId() == SLAB && (target.getDamage() & 0x08) == 0 && (target.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(target, Block.get(Item.DOUBLE_SLAB, this.meta), true);

                return true;
            } else if (block.getId() == SLAB && (block.getDamage() & 0x07) == (this.meta & 0x07)) {
                this.getLevel().setBlock(block, Block.get(Item.DOUBLE_SLAB, this.meta), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block.getId() == SLAB) {
                if ((block.getDamage() & 0x07) == (this.meta & 0x07)) {
                    this.getLevel().setBlock(block, Block.get(Item.DOUBLE_SLAB, this.meta), true);

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
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{this.getId(), this.meta & 0x07, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public Color getMapColor() {
        switch (this.meta & 0x07) {
            case Slab.STONE:
                return Color.stoneColor;
            case Slab.SANDSTONE:
                return Color.sandColor;
            case Slab.WOODEN:
                return Color.woodColor;
            case Slab.COBBLESTONE:
                return Color.stoneColor;
            case Slab.BRICK:
                return Color.stoneColor;
            case Slab.STONE_BRICK:
                return Color.stoneColor;
            case Slab.QUARTZ:
                return Color.quartzColor;
            case Slab.NETHER_BRICK:
                return Color.netherrackColor;

            default:
                return Color.stoneColor;     //unreachable
        }
    }
}
