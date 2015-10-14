package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ContainerInventory extends BaseInventory {
    public ContainerInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items) {
        super(holder, type, items);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize) {
        super(holder, type, items, overrideSize);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize, String overrideTitle) {
        super(holder, type, items, overrideSize, overrideTitle);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowid = (byte) who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        pk.slots = (short) this.getSize();
        InventoryHolder holder = this.getHolder();
        if (holder instanceof Vector3) {
            pk.x = (int) ((Vector3) holder).x;
            pk.y = (int) ((Vector3) holder).y;
            pk.z = (int) ((Vector3) holder).z;
        } else {
            pk.x = pk.y = pk.z = 0;
        }

        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowid = (byte) who.getWindowId(this);
        who.dataPacket(pk);
        super.onClose(who);
    }
}
