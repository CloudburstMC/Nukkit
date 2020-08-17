package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockStone extends BlockSolidMeta {
    public static final int NORMAL = 0;
    public static final int GRANITE = 1;
    public static final int POLISHED_GRANITE = 2;
    public static final int DIORITE = 3;
    public static final int POLISHED_DIORITE = 4;
    public static final int ANDESITE = 5;
    public static final int POLISHED_ANDESITE = 6;


    public BlockStone() {
        this(0);
    }

    public BlockStone(int meta) {
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
        return ItemTool.TYPE_PICKAXE;
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
        return names[this.getDamage() & 0x07];
    }

    @Override
    public BlockColor getColor() {
        BlockColor[] colors = new BlockColor[]{
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.DIRT_BLOCK_COLOR,
                BlockColor.DIRT_BLOCK_COLOR,
                BlockColor.QUARTZ_BLOCK_COLOR,
                BlockColor.QUARTZ_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR
        };
        return colors[this.getDamage() & 0x07];
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(this.getDamage() == 0 ? Item.COBBLESTONE : Item.STONE, this.getDamage(), 1)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
