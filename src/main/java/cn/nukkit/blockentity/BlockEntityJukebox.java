package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private Item recordItem;

    public BlockEntityJukebox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("RecordItem")) {
            this.recordItem = NBTIO.getItemHelper(namedTag.getCompound("RecordItem"));
        } else {
            this.recordItem = Item.get(0);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == Block.JUKEBOX;
    }

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem.clone();
        setDirty();
    }

    public Item getRecordItem() {
        return recordItem;
    }

    public void play() {
        if (this.recordItem instanceof ItemRecord) {
            PlaySoundPacket pk = new PlaySoundPacket();
            pk.name = ((ItemRecord) this.recordItem).getSoundId();
            pk.volume = 1;
            pk.pitch = 1;
            pk.x = (int) this.x;
            pk.y = (int) this.y;
            pk.z = (int) this.z;
            this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
        }
    }

    public void stop() {
        if (this.recordItem instanceof ItemRecord) {
            StopSoundPacket pk = new StopSoundPacket();
            pk.name = ((ItemRecord) this.recordItem).getSoundId();
            this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
        }
    }

    public void dropItem() {
        if (this.recordItem.getId() != 0) {
            stop();
            this.level.dropItem(this.up(), this.recordItem);
            this.recordItem = Item.get(0);
            setDirty();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, JUKEBOX);
    }

    @Override
    public void onBreak() {
        this.dropItem();
    }

    public int getComparatorSignal() {
        if (this.recordItem instanceof ItemRecord) {
            switch (this.recordItem.getId()) {
                case Item.RECORD_13:
                    return 1;
                case Item.RECORD_CAT:
                    return 2;
                case Item.RECORD_BLOCKS:
                    return 3;
                case Item.RECORD_CHIRP:
                    return 4;
                case Item.RECORD_FAR:
                    return 5;
                case Item.RECORD_MALL:
                    return 6;
                case Item.RECORD_MELLOHI:
                    return 7;
                case Item.RECORD_STAL:
                    return 8;
                case Item.RECORD_STRAD:
                    return 9;
                case Item.RECORD_WARD:
                    return 10;
                case Item.RECORD_11:
                    return 11;
                case Item.RECORD_WAIT:
                    return 12;
                case Item.RECORD_PIGSTEP:
                    return 13;
                case Item.RECORD_OTHERSIDE:
                    return 14;
                case Item.RECORD_5:
                case Item.RECORD_RELIC:
                    return 15;
            }
        }
        return 0;
    }
}
