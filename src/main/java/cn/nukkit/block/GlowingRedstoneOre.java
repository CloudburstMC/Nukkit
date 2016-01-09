package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

//和pm源码有点出入，这里参考了wiki

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GlowingRedstoneOre extends RedstoneOre {

    public GlowingRedstoneOre() {
        this(0);
    }

    public GlowingRedstoneOre(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Glowing Redstone Ore";
    }

    @Override
    public int getId() {
        return GLOWING_REDSTONE_ORE;
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            this.getLevel().setBlock(this, Block.get(Item.REDSTONE_ORE, this.meta), false, false);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }
}
