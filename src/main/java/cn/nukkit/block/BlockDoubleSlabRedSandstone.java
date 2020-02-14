package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.STONE_SLAB2;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockDoubleSlabRedSandstone extends BlockSolid {

    public BlockDoubleSlabRedSandstone(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem() {
        return Item.get(STONE_SLAB2, this.getMeta() & 0x07);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(STONE_SLAB2, this.getMeta() & 0x07, 2)
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
        switch (this.getMeta() & 0x07) {
            case 0:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 1:
                return BlockColor.PURPLE_BLOCK_COLOR;
            default:
                return BlockColor.STONE_BLOCK_COLOR;
        }
    }
}