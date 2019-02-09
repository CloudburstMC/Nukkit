package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
            switch (this.recordItem.getId()) {
                case Item.RECORD_13:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_13);
                    break;
                case Item.RECORD_CAT:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_CAT);
                    break;
                case Item.RECORD_BLOCKS:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_BLOCKS);
                    break;
                case Item.RECORD_CHIRP:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_CHIRP);
                    break;
                case Item.RECORD_FAR:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_FAR);
                    break;
                case Item.RECORD_MALL:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_MALL);
                    break;
                case Item.RECORD_MELLOHI:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_MELLOHI);
                    break;
                case Item.RECORD_STAL:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_STAL);
                    break;
                case Item.RECORD_STRAD:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_STRAD);
                    break;
                case Item.RECORD_WARD:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_WARD);
                    break;
                case Item.RECORD_11:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_11);
                    break;
                case Item.RECORD_WAIT:
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RECORD_WAIT);
                    break;
            }
        }
    }

    public void stop() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_STOP_RECORD);
    }

    public void dropItem() {
        if (this.recordItem.getId() != 0) {
            stop();
            this.level.dropItem(this.up(), this.recordItem);
            this.recordItem = Item.get(0);
        }
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
