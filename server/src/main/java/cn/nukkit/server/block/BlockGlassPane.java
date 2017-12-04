package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockGlassPane extends BlockThin {

    public BlockGlassPane() {
        this(0);
    }

    public BlockGlassPane(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glass Pane";
    }

    @Override
    public int getId() {
        return GLASS_PANE;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
