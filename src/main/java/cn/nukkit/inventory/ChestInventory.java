package cn.nukkit.inventory;

import cn.nukkit.blockentity.Chest;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

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
            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.CHEST_OPEN);
                ContainerInventory.sendBlockEventPacket(this.getHolder(), 1);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.CHEST_CLOSED);
                ContainerInventory.sendBlockEventPacket(this.getHolder(), 0);
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
