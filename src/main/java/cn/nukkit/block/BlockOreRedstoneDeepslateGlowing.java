package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;

public class BlockOreRedstoneDeepslateGlowing extends BlockOreRedstoneDeepslate {

    public BlockOreRedstoneDeepslateGlowing() {
    }

    @Override
    public int getId() {
        return LIT_DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    public String getName() {
        return "Glowing Deepslate Redstone Ore";
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(DEEPSLATE_REDSTONE_ORE));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            BlockFadeEvent event = new BlockFadeEvent(this, Block.get(DEEPSLATE_REDSTONE_ORE));
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewState(), false, false);
            }

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }
}
