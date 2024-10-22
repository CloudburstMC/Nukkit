package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockDoubleSlabRedSandstone extends BlockDoubleSlabBase {

    private static final String[] NAMES = {
            "Red Sandstone",
            "Purpur",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    public BlockDoubleSlabRedSandstone() {
        this(0);
    }

    public BlockDoubleSlabRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_RED_SANDSTONE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return RED_SANDSTONE_SLAB;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getSlabName() {
        return NAMES[this.getDamage() & 0x07];
    }

    @Override
    public int getItemDamage() {
        return this.getDamage() & 0x07;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            case 0:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 1:
                return BlockColor.PURPLE_BLOCK_COLOR;
            default:
                return BlockColor.STONE_BLOCK_COLOR;
        }
    }
}
