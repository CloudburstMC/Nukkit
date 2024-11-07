package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockNetherSprouts extends BlockRoots {

    public BlockNetherSprouts() {
        this(0);
    }

    public BlockNetherSprouts(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Nether Sprouts";
    }

    @Override
    public int getId() {
        return NETHER_SPROUTS_BLOCK;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.NETHER_SPROUTS);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{ toItem() };
        }
        return new Item[0];
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }
}
