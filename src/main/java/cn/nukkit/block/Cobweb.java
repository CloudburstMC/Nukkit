package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Cobweb extends Flowable {
    public Cobweb(){
        this(0);
    }

    public Cobweb(int meta){
        super(0);
    }

    @Override
    public String getName() {
        return "Cobweb";
    }

    @Override
    public int getId() {
        return COBWEB;
    }

    @Override
    public double getHardness() {
        return 4;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SWORD;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.STRING, 0, 1}
        };
    }
}
