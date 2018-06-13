package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockShulkerBox extends BlockSolidMeta {

    public BlockShulkerBox() {
        this(0);
    }

    public BlockShulkerBox(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Shulker Box";
    }

    @Override
    public double getHardness() {
        return 6;
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
}
