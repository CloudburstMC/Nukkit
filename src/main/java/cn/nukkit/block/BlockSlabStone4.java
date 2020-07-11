package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.values.StoneSlab4Type;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

public class BlockSlabStone4 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab4Type.PROPERTY,
            TOP_SLOT_PROPERTY
    );

    public static final int MOSSY_STONE_BRICKS = 0;
    public static final int SMOOTH_QUARTZ = 1;
    public static final int STONE = 2;
    public static final int CUT_SANDSTONE = 3;
    public static final int CUT_RED_SANDSTONE = 4;


    public BlockSlabStone4() {
        this(0);
    }

    public BlockSlabStone4(int meta) {
        super(meta, DOUBLE_STONE_SLAB4);
    }

    @Override
    public int getId() {
        return STONE_SLAB4;
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Mossy Stone Brick",
                "Smooth Quartz",
                "Stone",
                "Cut Sandstone",
                "Cut Red Sandstone",
                "",
                "",
                ""
        };

        return ((this.getDamage() & 0x08) > 0 ? "Upper " : "") + names[this.getDamage() & 0x07] + " Slab";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && slab.getPropertyValue(StoneSlab4Type.PROPERTY).equals(slab.getPropertyValue(StoneSlab4Type.PROPERTY));
    }


    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case MOSSY_STONE_BRICKS:
            case STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case SMOOTH_QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case CUT_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case CUT_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
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
