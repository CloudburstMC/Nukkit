package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSlabWood extends BlockSlab {

    public BlockSlabWood() {
        this(0);
    }

    public BlockSlabWood(int meta) {
        this(meta, DOUBLE_WOODEN_SLAB);
    }

    public BlockSlabWood(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    private static final String[] NAMES = {
            "Oak",
            "Spruce",
            "Birch",
            "Jungle",
            "Acacia",
            "Dark Oak",
            "",
            ""
    };

    @Override
    public String getName() {
        return (((this.getDamage() & 0x08) == 0x08) ? "Upper " : "") + NAMES[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public int getId() {
        return WOOD_SLAB;
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
        int damage = this.getDamage() & 0x07;
        return new ItemBlock(Block.get(this.getId(), damage), damage);
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x07) {
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
}
