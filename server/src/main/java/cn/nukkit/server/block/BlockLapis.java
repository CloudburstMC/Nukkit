package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLapis extends BlockSolid {


    public BlockLapis() {
        this(0);
    }

    public BlockLapis(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAPIS_BLOCK;
    }

    @Override
    public String getName() {
        return "Lapis Lazuli BlockType";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAPIS_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
