package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.values.StoneSlab3Type;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

public class BlockSlabStone3 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab3Type.PROPERTY,
            TOP_SLOT_PROPERTY
    );
    
    public static final int END_STONE_BRICKS = 0;
    public static final int SMOOTH_RED_SANDSTONE = 1;
    public static final int POLISHED_ANDESITE = 2;
    public static final int ANDESITE = 3;
    public static final int DIORITE = 4;
    public static final int POLISHED_DIORITE = 5;
    public static final int GRANITE = 6;
    public static final int POLISHED_GRANITE = 7;

    public BlockSlabStone3() {
        this(0);
    }

    public BlockSlabStone3(int meta) {
        super(meta, DOUBLE_STONE_SLAB3);
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "End Stone Brick",
                "Smooth Red Sandstone",
                "Polished Andesite",
                "Andesite",
                "Diorite",
                "Polished Diorite",
                "Granite",
                "Polisehd Granite"
        };

        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + names[this.getDamage() & 0x07] + " Slab";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && slab.getPropertyValue(StoneSlab3Type.PROPERTY).equals(slab.getPropertyValue(StoneSlab3Type.PROPERTY));
    }


    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            case END_STONE_BRICKS:
                return BlockColor.SAND_BLOCK_COLOR;
            case SMOOTH_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            default:
            case POLISHED_ANDESITE:
            case ANDESITE:
                return BlockColor.STONE_BLOCK_COLOR;
            case DIORITE:
            case POLISHED_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case GRANITE:
            case POLISHED_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
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
    public boolean canHarvestWithHand() {
        return false;
    }
}
