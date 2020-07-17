package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;

public abstract class BlockNylium extends BlockSolid {
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !up().isTransparent()) {
            level.setBlock(this, Block.get(NETHERRACK), false);
            return type;
        }
        return 0;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{ Item.get(NETHERRACK) };
        }
        return new Item[0];
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
