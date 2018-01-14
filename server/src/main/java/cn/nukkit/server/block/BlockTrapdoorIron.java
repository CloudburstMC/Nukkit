package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoorIron extends BlockTrapdoor {

    public BlockTrapdoorIron() {
        this(0);
    }

    public BlockTrapdoorIron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return IRON_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Iron Trapdoor";
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 25;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
