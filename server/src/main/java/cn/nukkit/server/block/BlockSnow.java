package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemSnowball;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

public class BlockSnow extends BlockSolid {

    public BlockSnow() {
        this(0);
    }

    public BlockSnow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Snow BlockType";
    }

    @Override
    public int getId() {
        return SNOW_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    new ItemSnowball(0, 4)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }


    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
