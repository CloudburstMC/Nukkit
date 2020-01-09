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
        super(meta, DOUBLE_STONE_SLAB);
    }

    @Override
    public int getId() {
        return STONE_SLAB;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone",
                "Sandstone",
                "Wooden",
                "Cobblestone",
                "Brick",
                "Stone Brick",
                "Quartz",
                "Nether Brick"
        };

        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + names[this.getDamage() & 0x07] + " Slab";
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
        return new ItemBlock(this, this.getDamage() & 0x07);
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