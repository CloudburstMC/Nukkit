package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Stonecutter extends Solid {
    public Stonecutter() {
        this(0);
    }

    public Stonecutter(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONECUTTER;
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.craftingType = 2;
        }
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{
                    {Item.STONECUTTER, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }
}
