package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Planks extends Solid {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;


    public Planks() {
        this(0);
    }

    public Planks(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOODEN_PLANKS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Wood Planks",
                "Spruce Wood Planks",
                "Birch Wood Planks",
                "Jungle Wood Planks",
                "Acacia Wood Planks",
                "Jungle Wood Planks",
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.woodColor;
    }
}
