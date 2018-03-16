package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryCloseEvent;

import static cn.nukkit.Player.CRAFTING_SMALL;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerClosePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int windowId;

    @Override
    public void decode() {
        this.windowId = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
    }

    @Override
    public void handle(Player player) {
        if (!player.spawned || this.windowId == 0) {
            return;
        }
        player.craftingType = CRAFTING_SMALL;
        player.resetCraftingGridType();

        if (player.windowIndex.containsKey(this.windowId)) {
            player.server.getPluginManager().callEvent(new InventoryCloseEvent(player.windowIndex.get(this.windowId), player));
            player.removeWindow(player.windowIndex.get(this.windowId));
        } else {
            player.windowIndex.remove(this.windowId);
        }
    }
}
