package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StoneBricks extends Solid {
    public static final int NORMAL = 0;
    public static final int MOSSY = 1;
    public static final int CRACKED = 2;
    public static final int CHISELED = 3;

    protected int id = STONE_BRICKS;

    public StoneBricks() {
        this(0);
    }

    public StoneBricks(int meta) {
        super(STONE_BRICKS, meta);
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone Bricks",
                "Mossy Stone Bricks",
                "Cracked Stone Bricks",
                "Chiseled Stone Bricks"
        };

        return names[this.meta & 0x03];
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
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.STONE_BRICKS, this.meta & 0x03, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }
}
