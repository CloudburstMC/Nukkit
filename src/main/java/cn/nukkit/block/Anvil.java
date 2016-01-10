package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class Anvil extends Fallable {

    public Anvil() {
        this(0);
    }

    public Anvil(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return ANVIL;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public String getName() {
        return "Anvil";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            if (player.isCreative()) {
                return true;
            }
            player.addWindow(new AnvilInventory(this));
        }
        return true;
    }


    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{new int[]{this.getId(), 0, 1}}; //TODO break level
        } else {
            return new int[0][];
        }
    }

    @Override
    public Color getMapColor() {
        return Color.ironColor;
    }

}
