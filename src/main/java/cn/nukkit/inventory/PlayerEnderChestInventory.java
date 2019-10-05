package cn.nukkit.inventory;

import cn.nukkit.block.BlockEnderChest;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;

public class PlayerEnderChestInventory extends BaseInventory {

    public PlayerEnderChestInventory(EntityHumanType player) {
        super(player, InventoryType.ENDER_CHEST);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who != this.getHolder()) {
            return;
        }
        super.onOpen(who);
        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.windowId = who.getWindowId(this);
        containerOpenPacket.type = this.getType().getNetworkType();
        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null) {
            containerOpenPacket.x = (int) chest.getX();
            containerOpenPacket.y = (int) chest.getY();
            containerOpenPacket.z = (int) chest.getZ();
        } else {
            containerOpenPacket.x = containerOpenPacket.y = containerOpenPacket.z = 0;
        }

        who.dataPacket(containerOpenPacket);

        this.sendContents(who);

        if (chest != null && chest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.case1 = 1;
            blockEventPacket.case2 = 2;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_ENDERCHEST_OPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.windowId = who.getWindowId(this);
        who.dataPacket(containerClosePacket);
        super.onClose(who);

        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null && chest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.case1 = 1;
            blockEventPacket.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_ENDERCHEST_CLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }

            who.setViewingEnderChest(null);
        }

        super.onClose(who);
    }
}
