package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

public class BlockStonecutter extends BlockSolid {

    public BlockStonecutter(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
