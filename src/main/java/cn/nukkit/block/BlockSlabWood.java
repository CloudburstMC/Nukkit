package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.DOUBLE_WOODEN_SLAB;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSlabWood extends BlockSlab {

    public BlockSlabWood(Identifier id) {
        super(id);
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    public Item toItem() {
        return Item.get(id, this.getMeta() & 0x07);
    }

    @Override
    public BlockColor getColor() {
        switch (getMeta() & 0x07) {
            default:
            case 0: //OAK
                return BlockColor.WOOD_BLOCK_COLOR;
            case 1: //SPRUCE
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case 2: //BIRCH
                return BlockColor.SAND_BLOCK_COLOR;
            case 3: //JUNGLE
                return BlockColor.DIRT_BLOCK_COLOR;
            case 4: //ACACIA
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 5: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

    @Override
    protected Identifier getDoubleSlab() {
        return DOUBLE_WOODEN_SLAB;
    }
}
