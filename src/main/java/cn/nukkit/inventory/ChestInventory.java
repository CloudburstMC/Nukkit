package cn.nukkit.inventory;

import cn.nukkit.blockentity.Chest;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.BlockEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChestInventory extends ContainerInventory {

    protected DoubleChestInventory doubleInventory;

    public ChestInventory(Chest chest) {
        super(chest, InventoryType.CHEST);
    }

    @Override
    public Chest getHolder() {
        return (Chest) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            BlockEventPacket packet = new BlockEventPacket();
            packet.setBlockPosition(this.getHolder().getPosition());
            packet.setEventType(1);
            packet.setEventData(1);

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.CHEST_OPEN);
                level.addChunkPacket(this.getHolder().getPosition(), packet);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket packet = new BlockEventPacket();
            packet.setBlockPosition(this.getHolder().getPosition());
            packet.setEventType(1);
            packet.setEventData(0);

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.CHEST_CLOSED);
                level.addChunkPacket(this.getHolder().getPosition(), packet);
            }
        }

        super.onClose(who);
    }

    public void setDoubleInventory(DoubleChestInventory doubleInventory) {
        this.doubleInventory = doubleInventory;
    }

    public DoubleChestInventory getDoubleInventory() {
        return doubleInventory;
    }

    @Override
    public void sendSlot(int index, Player... players) {
        if (this.doubleInventory != null) {
            this.doubleInventory.sendSlot(this, index, players);
        } else {
            super.sendSlot(index, players);
        }
    }
}
