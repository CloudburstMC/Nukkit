package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.BlockEventPacket;

/**
 * Created by PetteriM1
 */
public class ShulkerBoxInventory extends ContainerInventory {

    public ShulkerBoxInventory(BlockEntityShulkerBox box) {
        super(box, InventoryType.SHULKER_BOX);
    }

    @Override
    public BlockEntityShulkerBox getHolder() {
        return (BlockEntityShulkerBox) this.holder;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 2;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXOPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXCLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }

        super.onClose(who);
    }

    @Override
    public boolean canAddItem(Item item) {
        if (item.getId() == BlockID.SHULKER_BOX || item.getId() == BlockID.UNDYED_SHULKER_BOX) {
            // Do not allow nested shulker boxes.
            return false;
        }
        return super.canAddItem(item);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        super.sendSlot(index, players);
    }
}
