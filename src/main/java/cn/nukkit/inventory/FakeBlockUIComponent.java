package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;

public class FakeBlockUIComponent extends PlayerUIComponent {
    private final InventoryType type;

    FakeBlockUIComponent(PlayerUIInventory playerUI, InventoryType type, int offset, Block block) {
        super(playerUI, offset, type.getDefaultSize());
        this.type = type;
        this.holder = new FakeBlockMenu(this, block);
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
        ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.setId(who.getWindowId(this));
        packet.setType(ContainerType.from(type.getNetworkType()));
        InventoryHolder holder = this.getHolder();
        if (holder != null) {
            packet.setBlockPosition(((FakeBlockMenu) holder).getPosition());
        } else {
            packet.setBlockPosition(Vector3i.ZERO);
        }

        who.sendPacket(packet);

        this.sendContents(who);
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.setId(who.getWindowId(this));
        who.sendPacket(packet);
        super.onClose(who);
    }
}
