package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabRedSandstone extends BlockSlab {

    public static final int RED_SANDSTONE = 0;
    public static final int PURPUR = 1;

    public BlockSlabRedSandstone() {
        this(RED_SANDSTONE);
    }

    public BlockSlabRedSandstone(int meta) {
        super(meta, DOUBLE_RED_SANDSTONE_SLAB);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE_SLAB;
    }

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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        int damage = this.getDamage() & 0x07;
        switch (damage) {
            case 0:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 1:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case 2:
                return BlockColor.CYAN_BLOCK_COLOR;
            case 3:
                return BlockColor.DIAMOND_BLOCK_COLOR;
            case 4:
                return BlockColor.CYAN_BLOCK_COLOR;
            case 5:
                return BlockColor.STONE_BLOCK_COLOR;
            case 6:
                return BlockColor.SAND_BLOCK_COLOR;
            case 7:
                return BlockColor.NETHERRACK_BLOCK_COLOR;
        }
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
