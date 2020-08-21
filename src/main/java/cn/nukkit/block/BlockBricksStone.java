package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockBricksStone extends BlockSolidMeta {
    public static final int NORMAL = 0;
    public static final int MOSSY = 1;
    public static final int CRACKED = 2;
    public static final int CHISELED = 3;

    public BlockBricksStone() {
        this(0);
    }

    public BlockBricksStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BRICKS;
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
    public String getName() {
        String[] names = new String[]{
                "Stone Bricks",
                "Mossy Stone Bricks",
                "Cracked Stone Bricks",
                "Chiseled Stone Bricks"
        };

        return names[this.getDamage() & 0x03];
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.STONE_BRICKS, this.getDamage() & 0x03, 1)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
