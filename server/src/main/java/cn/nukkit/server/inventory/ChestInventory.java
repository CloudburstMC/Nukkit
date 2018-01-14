package cn.nukkit.server.inventory;

import cn.nukkit.server.Player;
import cn.nukkit.server.blockentity.BlockEntityChest;
import cn.nukkit.server.level.NukkitLevel;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChestInventory extends ContainerInventory {

    public ChestInventory(BlockEntityChest chest) {
        super(chest, InventoryType.CHEST);
    }

    @Override
    public BlockEntityChest getHolder() {
        return (BlockEntityChest) this.holder;
    }

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

            NukkitLevel level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_CHEST_OPEN, 1, -1, this.getHolder().add(0.5, 0.5, 0.5));
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            NukkitLevel level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_CHEST_CLOSED, 1, -1, this.getHolder().add(0.5, 0.5, 0.5));
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }

        super.onClose(who);
    }
}
