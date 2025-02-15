package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockSandstone extends BlockSolidMeta {

    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int CUT = 2;
    public static final int SMOOTH = 3;

    public BlockSandstone() {
        this(0);
    }

    public BlockSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SANDSTONE;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 0.8;
    }

    private static final String[] NAMES = {
            "Sandstone",
            "Chiseled Sandstone",
            "Cut Sandstone",
            "Smooth Sandstone"
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.getDamage() & 0x03);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
