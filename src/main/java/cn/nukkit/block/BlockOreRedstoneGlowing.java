package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;

//和pm源码有点出入，这里参考了wiki

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockOreRedstoneGlowing extends BlockOreRedstone {

    public BlockOreRedstoneGlowing() {
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
    public Item toItem() {
        return new ItemBlock(new BlockOreRedstone());
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            BlockFadeEvent event = new BlockFadeEvent(this, get(REDSTONE_ORE));
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewBlock(), false, false);
            }

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
