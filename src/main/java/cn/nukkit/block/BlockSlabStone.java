package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabStone extends BlockSlab {

    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    public BlockSlabStone() {
        this(0);
    }

    public BlockSlabStone(int meta) {
        this(meta, DOUBLE_STONE_SLAB);
    }

    public BlockSlabStone(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return STONE_SLAB;
    }

    private static final String[] NAMES = {
            "Stone",
            "Sandstone",
            "Oak",
            "Cobblestone",
            "Brick",
            "Stone Brick",
            "Quartz",
            "Nether Brick"
    };

    @Override
    public String getName() {
        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + NAMES[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        int damage = this.getDamage() & 0x07;
        return new ItemBlock(Block.get(this.getId(), damage), damage);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            case NETHER_BRICK:
                return BlockColor.NETHERRACK_BLOCK_COLOR;
            default:
            case STONE:
            case COBBLESTONE:
            case BRICK:
            case STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case WOODEN:
                return BlockColor.WOOD_BLOCK_COLOR;
            case QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
