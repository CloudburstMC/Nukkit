package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBarrel;
import cn.nukkit.blockentity.Barrel;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

public class BarrelInventory extends ContainerInventory {

    public BarrelInventory(Barrel barrelEntity) {
        super(barrelEntity, InventoryType.BARREL);
    }

    @Override
    public Barrel getHolder() {
        return (Barrel) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            Barrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel) {
                    BlockBarrel blockBarrel = (BlockBarrel) block;
                    if (!blockBarrel.isOpen()) {
                        blockBarrel.setOpen(true);
                        level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.BARREL_OPEN);
                    }
                }
            }
        }
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        if (this.getViewers().isEmpty()) {
            Barrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel) {
                    BlockBarrel blockBarrel = (BlockBarrel) block;
                    if (blockBarrel.isOpen()) {
                        blockBarrel.setOpen(false);
                        level.addLevelSoundEvent(this.getHolder().getPosition(), SoundEvent.BARREL_CLOSE);
                    }
                }
            }
        }
    }
}