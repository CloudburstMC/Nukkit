package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.TileEventPacket;
import cn.nukkit.tile.Chest;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChestInventory extends ContainerInventory {

    public ChestInventory(Chest tile) {
        super(tile, InventoryType.get(InventoryType.CHEST));
    }

    @Override
    public Chest getHolder() {
        return (Chest) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            TileEventPacket pk = new TileEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 2;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            TileEventPacket pk = new TileEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
            }
        }

        super.onClose(who);
    }
}
