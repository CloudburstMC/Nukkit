package cn.nukkit.server.block;

import cn.nukkit.server.level.NukkitLevel;

//和pm源码有点出入，这里参考了wiki

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockOreRedstoneGlowing extends BlockOreRedstone {

    public BlockOreRedstoneGlowing() {
        this(0);
    }

    public BlockOreRedstoneGlowing(int meta) {
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
        if (type == NukkitLevel.BLOCK_UPDATE_SCHEDULED || type == NukkitLevel.BLOCK_UPDATE_RANDOM) {
            this.getLevel().setBlock(this, new BlockOreRedstone(this.meta), false, false);

            return NukkitLevel.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
