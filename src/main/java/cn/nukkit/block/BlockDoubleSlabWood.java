package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoubleSlabWood extends BlockSolidMeta {

    public BlockDoubleSlabWood() {
        this(0);
    }

    public BlockDoubleSlabWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_WOOD_SLAB;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak",
                "Spruce",
                "Birch",
                "Jungle",
                "Acacia",
                "Dark Oak",
                "",
                ""
        };
        return "Double " + names[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.WOODEN_SLAB), this.getDamage() & 0x07);
    }

    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.WOOD_SLAB, this.getDamage() & 0x07, 2)
        };
    }

    @Override
    public BlockColor getColor() {
        switch(this.getDamage() & 0x07){
            default:
                case 0: //OAK
                    return BlockColor.WOOD_BLOCK_COLOR;
            case 1: //SPRUCE
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case 2: //BIRCH
                return BlockColor.SAND_BLOCK_COLOR;
            case 3: //JUNGLE
                return BlockColor.DIRT_BLOCK_COLOR;
            case 4: //Acacia
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 5: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }
}
