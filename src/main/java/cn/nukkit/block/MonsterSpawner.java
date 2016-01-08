package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class MonsterSpawner extends Solid {

    public MonsterSpawner() {
        this(0);
    }

    public MonsterSpawner(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Monster Spawner";
    }

    @Override
    public int getId() {
        return MONSTER_SPAWNER;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{};
    }
}
