package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Stone extends Solid {
    public static final int NORMAL = 0;
    public static final int GRANITE = 1;
    public static final int POLISHED_GRANITE = 2;
    public static final int DIORITE = 3;
    public static final int POLISHED_DIORITE = 4;
    public static final int ANDESITE = 5;
    public static final int POLISHED_ANDESITE = 6;


    public Stone() {
        this(0);
    }

    public Stone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone",
                "Granite",
                "Polished Granite",
                "Diorite",
                "Polished Diorite",
                "Andesite",
                "Polished Andesite",
                "Unknown Stone"
        };
        return names[this.meta & 0x07];
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{this.getDamage() == 0 ? Item.COBBLESTONE : Item.STONE, this.getDamage(), 1}};
        } else {
            return new int[0][];
        }
    }

}
