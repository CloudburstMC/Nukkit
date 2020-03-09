package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockPrismarine extends BlockSolid {

    public static final int NORMAL = 0;
    public static final int BRICKS = 1;
    public static final int DARK = 2;

    public BlockPrismarine(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
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
    public BlockColor getColor() {
        switch (getMeta() & 0x07) {
            case NORMAL:
                return BlockColor.CYAN_BLOCK_COLOR;
            case BRICKS:
            case DARK:
                return BlockColor.DIAMOND_BLOCK_COLOR;
            default:
                return BlockColor.STONE_BLOCK_COLOR;
        }
    }
}
