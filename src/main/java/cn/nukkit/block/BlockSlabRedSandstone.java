package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.DOUBLE_STONE_SLAB2;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabRedSandstone extends BlockSlab {

    public static final int RED_SANDSTONE = 0;
    public static final int PURPUR = 1; //WHY THIS

    public BlockSlabRedSandstone(Identifier id) {
        super(id);
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
    public Item toItem() {
        return Item.get(id, this.getDamage() & 0x07);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected Identifier getDoubleSlab() {
        return DOUBLE_STONE_SLAB2;
    }
}