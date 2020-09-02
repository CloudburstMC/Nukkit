package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;


public class BlockPrismarine extends BlockSolidMeta {

    public static final int NORMAL = 0;
    public static final int DARK = 1;
    public static final int BRICKS = 2;

    private static final String[] NAMES = new String[]{
            "Prismarine",
            "Dark prismarine",
            "Prismarine bricks"
    };

    public BlockPrismarine() {
        this(0);
    }

    public BlockPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 2 ? 0 : this.getDamage()];
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x07) {
            case NORMAL:
                return BlockColor.CYAN_BLOCK_COLOR;
            case DARK:
            case BRICKS:
                return BlockColor.DIAMOND_BLOCK_COLOR;
            default:
                return BlockColor.STONE_BLOCK_COLOR;
        }
    }
}
