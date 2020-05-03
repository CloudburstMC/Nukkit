package cn.nukkit.inventory;

import cn.nukkit.block.BlockEnderChest;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;

public class PlayerEnderChestInventory extends BaseInventory {

    public PlayerEnderChestInventory(Human player) {
        super(player, InventoryType.ENDER_CHEST);
    }

    @Override
    public Human getHolder() {
        return (Human) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who != this.getHolder()) {
            return;
        }
        super.onOpen(who);
        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.setWindowId(who.getWindowId(this));
        containerOpenPacket.setType((byte) this.getType().getNetworkType());
        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null) {
            containerOpenPacket.setBlockPosition(chest.getPosition());
        } else {
            containerOpenPacket.setBlockPosition(Vector3i.ZERO);
        }

        who.sendPacket(containerOpenPacket);

        this.sendContents(who);

        if (chest != null && chest.getViewers().size() == 1) {
            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition().add(0.5, 0.5, 0.5), SoundEvent.ENDERCHEST_OPEN);
                ContainerInventory.sendBlockEventPacket(level.getBlockEntity(chest.getPosition()), 1);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.setWindowId(who.getWindowId(this));
        who.sendPacket(containerClosePacket);
        super.onClose(who);

        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null && chest.getViewers().size() == 1) {
            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().getPosition().add(0.5, 0.5, 0.5), SoundEvent.ENDERCHEST_CLOSED);
                ContainerInventory.sendBlockEventPacket(level.getBlockEntity(chest.getPosition()), 0);
            }

            who.setViewingEnderChest(null);
        }

        super.onClose(who);
    }
}
