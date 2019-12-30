package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.STONE_SLAB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoubleSlab extends BlockSolid {
    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int FAKE_WOOD = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    public BlockDoubleSlab(Identifier id) {
        super(id);
    }

    //todo hardness and residence

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(STONE_SLAB, this.getDamage() & 0x07, 2)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            case BlockDoubleSlab.STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockDoubleSlab.SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case BlockDoubleSlab.FAKE_WOOD:
                return BlockColor.WOOD_BLOCK_COLOR;
            case BlockDoubleSlab.COBBLESTONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockDoubleSlab.BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockDoubleSlab.STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case BlockDoubleSlab.QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case BlockDoubleSlab.NETHER_BRICK:
                return BlockColor.NETHERRACK_BLOCK_COLOR;

            default:
                return BlockColor.STONE_BLOCK_COLOR;     //unreachable
        }
    }
}
