package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Grass extends Solid {


    public Grass() {
        this(0);
    }

    public Grass(int meta) {
        super(GRASS, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Grass";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{Item.DIRT, 0, 1}};
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(Item.FARMLAND, 0), true);

            return true;
        }
        //todo:isShovel, Item.DYE growGrass
        return false;
    }

    //todo:onUpdate
}
