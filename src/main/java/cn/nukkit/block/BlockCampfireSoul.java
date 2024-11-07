package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;

public class BlockCampfireSoul extends BlockCampfire {

    public BlockCampfireSoul() {
        this(0);
    }

    public BlockCampfireSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_CAMPFIRE_BLOCK;
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished()? 0 : 10;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SOUL_CAMPFIRE);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ new ItemBlock(Block.get(Block.SOUL_SOIL, 0), 0) };
    }
}
