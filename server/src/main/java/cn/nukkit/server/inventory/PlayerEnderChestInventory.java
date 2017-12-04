package cn.nukkit.server.inventory;

import cn.nukkit.server.Player;
import cn.nukkit.server.block.BlockEnderChest;
import cn.nukkit.server.entity.EntityHuman;
import cn.nukkit.server.entity.EntityHumanType;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.network.protocol.BlockEventPacket;
import cn.nukkit.server.network.protocol.ContainerClosePacket;
import cn.nukkit.server.network.protocol.ContainerOpenPacket;
import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

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
                level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_CHEST_OPEN, 1, -1, this.getHolder().add(0.5, 0.5, 0.5));
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
                level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_CHEST_CLOSED, 1, -1, this.getHolder().add(0.5, 0.5, 0.5));
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }

            who.setViewingEnderChest(null);
        }

        super.onClose(who);
    }
}
