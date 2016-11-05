package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockBeacon extends BlockSolid {

    public BlockBeacon() {
        this(0);
    }

    public BlockBeacon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEACON;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        // TODO handle GUI
        return super.onActivate(item, player);
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.BEACON, 0, 1}};
    }

}
