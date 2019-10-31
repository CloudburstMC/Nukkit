package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.CompletedUsingItemPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemEdible extends Item {
    public ItemEdible(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public ItemEdible(int id) {
        super(id);
    }

    public ItemEdible(int id, Integer meta) {
        super(id, meta);
    }

    public ItemEdible(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.getFoodData().getLevel() != player.getFoodData().getMaxLevel();
    }

    @Override
    public int completeAction(Player player, int ticksUsed) {
        if (consume(player)) {
            return CompletedUsingItemPacket.ACTION_EAT;
        }
        return CompletedUsingItemPacket.ACTION_UNKNOWN;
    }

    protected boolean consume(Player player) {
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        Food food = Food.getByRelative(this);
        if (food != null && food.eatenBy(player)) --this.count;
        player.getInventory().setItemInHand(this);
        return true;
    }
}
