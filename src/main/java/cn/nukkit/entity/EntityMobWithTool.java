package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;

import javax.annotation.Nullable;

/**
 * A mob that can hold tools in its hands
 */
public interface EntityMobWithTool {

    @Nullable
    Item getTool();

    @SuppressWarnings("unused")
    void setTool(@Nullable Item tool);

    default int getToolSlot() {
        return 0;
    }

    @Nullable
    Item getOffhand();

    @SuppressWarnings("unused")
    void setOffhand(@Nullable Item offhand);

    default int getOffhandSlot() {
        return 1;
    }

    default void sendHandItems(Player p) {
        sendHandItems(p, false);
    }

    /**
     * Update mob's held items to player
     * @param player player
     * @param sendAir whether empty (air) slots should be sent
     */
    default void sendHandItems(Player player, boolean sendAir) {
        Item tool = this.getTool();

        if (tool != null && (sendAir || !tool.isNull())) {
            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.eid = ((Entity) this).getId();
            pk.hotbarSlot = this.getToolSlot();
            pk.item = tool;
            player.dataPacket(pk);
        }

        Item offhand = this.getOffhand();

        if (offhand != null && (sendAir || !offhand.isNull())) {
            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.eid = ((Entity) this).getId();
            pk.hotbarSlot = this.getOffhandSlot();
            pk.item = offhand;
            player.dataPacket(pk);
        }
    }
}
