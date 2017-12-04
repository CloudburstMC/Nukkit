package cn.nukkit.server.blockentity;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemRecord;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private Item recordItem;

    public BlockEntityJukebox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.recordItem = NBTIO.getItemHelper(nbt.getCompound("RecordItem"));
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.JUKEBOX;
    }

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem;
    }

    public Item getRecordItem() {
        return recordItem;
    }

    public void play() {
        if (this.recordItem instanceof ItemRecord) {
            LevelSoundEventPacket pk = new LevelSoundEventPacket();
            pk.sound = ((ItemRecord) this.recordItem).getSoundId();
            pk.pitch = 1;
            pk.extraData = -1;
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;

            NukkitServer.broadcastPacket(this.level.getPlayers().values(), pk);
        }
    }

    public void dropItem() {
        this.level.dropItem(this.up(), this.recordItem);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, JUKEBOX)
                .putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }
}
