package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

public class BlockChiseledBookshelf extends BlockSolidMeta {

    public BlockChiseledBookshelf() {
        this(0);
    }

    public BlockChiseledBookshelf(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Chiseled Bookshelf";
    }

    @Override
    public int getId() {
        return CHISELED_BOOKSHELF;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 7.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{new ItemBlock(Block.get(this.getId(), 0), 0)};
        }
        return new Item[]{Item.get(AIR)};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
