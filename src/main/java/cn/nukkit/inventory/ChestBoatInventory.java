package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerOpenPacket;

public class ChestBoatInventory extends ContainerInventory {

    public ChestBoatInventory(EntityChestBoat holder) {
        super(holder, InventoryType.CHEST_BOAT);
    }

    @Override
    public EntityChestBoat getHolder() {
        return (EntityChestBoat) super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);

        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = InventoryType.CHEST_BOAT.getNetworkType();
        InventoryHolder holder = this.getHolder();
        if (holder != null) {
            pk.x = (int) ((Vector3) holder).getX();
            pk.y = (int) ((Vector3) holder).getY();
            pk.z = (int) ((Vector3) holder).getZ();
        } else {
            pk.x = pk.y = pk.z = 0;
        }

        if (holder != null) {
            pk.entityId = ((Entity) holder).getId();
        }

        who.dataPacket(pk);

        this.sendContents(who);
    }
}
