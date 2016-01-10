package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Quartz extends Solid {
    
    public static final int QUARTZ_NORMAL = 0;
    public static final int QUARTZ_CHISELED = 1;
    public static final int QUARTZ_PILLAR = 2;
    public static final int QUARTZ_PILLAR2 = 3;


    public Quartz() {
        this(0);
    }

    public Quartz(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return QUARTZ_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Quartz Block",
                "Chiseled Quartz Block",
                "Quartz Pillar",
                "Quartz Pillar"
        };

        return names[this.meta & 0x03];
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        short[] faces = new short[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100
        };

        this.meta = ((this.meta & 0x03) | faces[face]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{Item.QUARTZ_BLOCK, this.meta & 0x03, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.quartzColor;
    }
}
