package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;

public class FakeBlockUIComponent extends PlayerUIComponent {

    private final InventoryType type;

    FakeBlockUIComponent(PlayerUIInventory playerUI, InventoryType type, int offset, Position position) {
        super(playerUI, offset, type.getDefaultSize());
        this.type = type;
        this.holder = new FakeBlockMenu(this, position);
    }


    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    @Override
    public boolean open(Player who) {
        InventoryOpenEvent ev = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = type.getNetworkType();
        InventoryHolder holder = this.getHolder();
        if (holder != null) {
            pk.x = (int) ((Vector3) holder).getX();
            pk.y = (int) ((Vector3) holder).getY();
            pk.z = (int) ((Vector3) holder).getZ();
        } else {
            pk.x = pk.y = pk.z = 0;
        }

        who.dataPacket(pk);

        this.sendContents(who);
    }

    @Override
    public void onClose(Player who) {
        who.resetCraftingGridType(); // Handles moving of UI inventory contents

        if (who.getClosingWindowId() != Integer.MAX_VALUE) {
            ContainerClosePacket pk = new ContainerClosePacket();
            int id = who.getWindowId(this);
            pk.wasServerInitiated = id != who.getClosingWindowId();
            pk.windowId = pk.wasServerInitiated ? id : who.getClosingWindowId();
            who.dataPacket(pk);
        }

        super.onClose(who);
    }

    @Override
    public void close(Player who) {
        InventoryCloseEvent ev = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        this.onClose(who);
    }
}
