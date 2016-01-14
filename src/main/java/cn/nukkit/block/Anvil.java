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
        super(meta);
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
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Anvil",
                "Anvil",
                "Anvil",
                "Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil"
        };
        return names[this.meta];
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            int faces[] = {0, 1, 2, 3};
            int damage = this.getDamage();
            this.meta = faces[player != null ? player.getDirection() : 0] & 0x04;
            if (damage >= 0 && damage <= 3) {
                this.meta = faces[player != null ? player.getDirection() : 0];
            } else if (damage >= 4 && damage <= 7) {
                this.meta = faces[player != null ? player.getDirection() : 0] | 0x04;
            } else if (damage >= 8 && damage <= 11) {
                this.meta = faces[player != null ? player.getDirection() : 0] | 0x08;
            }
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
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
        int damage = this.getDamage();
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            if (damage >= 0 && damage <= 3) { //Anvil
                return new int[][]{new int[]{this.getId(), 0, 1}};
            } else if (damage >= 4 && damage <= 7) { //Slightly Anvil
                return new int[][]{new int[]{this.getId(), this.meta & 0x04, 1}};
            } else if (damage >= 8 && damage <= 11) { //Very Damaged Anvil
                return new int[][]{new int[]{this.getId(), this.meta & 0x08, 1}};
            }
        } else {
            return new int[0][];
        }
        return new int[0][];
    }

    @Override
    public Color getMapColor() {
        return Color.ironColor;
    }

}
