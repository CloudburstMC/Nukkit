package cn.nukkit.block;

import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.world.World;

/**
 * @author Pub4Game
 */
public class BlockRedstoneLampLit extends BlockRedstoneLamp {

    public BlockRedstoneLampLit() {
    }

    @Override
    public String getName() {
        return "Lit Redstone Lamp";
    }

    @Override
    public int getId() {
        return LIT_REDSTONE_LAMP;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_LAMP));
    }

    @Override
    public int onUpdate(int type) {
        if ((type == World.BLOCK_UPDATE_NORMAL || type == World.BLOCK_UPDATE_REDSTONE) && !this.world.isBlockPowered(this.getLocation())) {
            // Redstone event
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            getWorld().getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }
            this.world.scheduleUpdate(this, 4);
            return 1;
        }

        if (type == World.BLOCK_UPDATE_SCHEDULED && !this.world.isBlockPowered(this.getLocation())) {
            this.world.setBlock(this, Block.get(BlockID.REDSTONE_LAMP), false, false);
        }
        return 0;
    }
}
