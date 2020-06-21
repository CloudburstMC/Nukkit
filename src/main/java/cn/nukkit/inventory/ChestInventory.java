package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.event.block.BlockChestOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChestInventory extends ContainerInventory {

    protected DoubleChestInventory doubleInventory;

    public ChestInventory(BlockEntityChest chest) {
        super(chest, InventoryType.CHEST);
    }

    @Override
    public BlockEntityChest getHolder() {
        return (BlockEntityChest) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (openOnce && opennedOnce){
            int empty = 0;
            for (Map.Entry<Integer, Item> entry : getContents().entrySet()) {
                if (entry.getValue() == null || entry.getValue().getId() == 0) empty++;
            }
            if (empty == getContents().size()) return;
        }
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
                level.addLevelSoundEvent(this.getHolder().add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_CHEST_OPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
            opennedOnce = true;
        }
        BlockChestOpenEvent event =  new BlockChestOpenEvent(who,this);
        Server.getInstance().getPluginManager().callEvent(event);
    }
    public boolean notClose;
    public boolean openOnce;
    private boolean opennedOnce;

    @Override
    public void onClose(Player who) {
        if (!notClose && this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_CHEST_CLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }

        super.onClose(who);
    }

    public void closeAll(){
        if (!opennedOnce) return;
        BlockEventPacket pk = new BlockEventPacket();
        pk.x = (int) this.getHolder().getX();
        pk.y = (int) this.getHolder().getY();
        pk.z = (int) this.getHolder().getZ();
        pk.case1 = 1;
        pk.case2 = 0;

        Level level = this.getHolder().getLevel();
        level.addLevelSoundEvent(this.getHolder().add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_CHEST_CLOSED);
        level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
        opennedOnce = false;
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
