package cn.nukkit.blockentity;

import cn.nukkit.block.BlockIds;
import cn.nukkit.item.Item;
import cn.nukkit.item.RecordItem;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.item.ItemIds.*;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private static final Map<Identifier, Integer> LEVEL_EVENT_MAP = new IdentityHashMap<>();

    static {
        LEVEL_EVENT_MAP.put(RECORD_13, LevelSoundEventPacket.SOUND_RECORD_13);
        LEVEL_EVENT_MAP.put(RECORD_CAT, LevelSoundEventPacket.SOUND_RECORD_CAT);
        LEVEL_EVENT_MAP.put(RECORD_BLOCKS, LevelSoundEventPacket.SOUND_RECORD_BLOCKS);
        LEVEL_EVENT_MAP.put(RECORD_CHIRP, LevelSoundEventPacket.SOUND_RECORD_CHIRP);
        LEVEL_EVENT_MAP.put(RECORD_FAR, LevelSoundEventPacket.SOUND_RECORD_FAR);
        LEVEL_EVENT_MAP.put(RECORD_MALL, LevelSoundEventPacket.SOUND_RECORD_MALL);
        LEVEL_EVENT_MAP.put(RECORD_MELLOHI, LevelSoundEventPacket.SOUND_RECORD_MELLOHI);
        LEVEL_EVENT_MAP.put(RECORD_STAL, LevelSoundEventPacket.SOUND_RECORD_STAL);
        LEVEL_EVENT_MAP.put(RECORD_STRAD, LevelSoundEventPacket.SOUND_RECORD_STRAD);
        LEVEL_EVENT_MAP.put(RECORD_WARD, LevelSoundEventPacket.SOUND_RECORD_WARD);
        LEVEL_EVENT_MAP.put(RECORD_11, LevelSoundEventPacket.SOUND_RECORD_11);
        LEVEL_EVENT_MAP.put(RECORD_WAIT, LevelSoundEventPacket.SOUND_RECORD_WAIT);
    }

    private Item recordItem;

    public BlockEntityJukebox(Chunk chunk, CompoundTag nbt) {
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
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == BlockIds.JUKEBOX;
    }

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem;
    }

    public Item getRecordItem() {
        return recordItem;
    }

    public void play() {
        if (this.recordItem instanceof RecordItem) {
            this.getLevel().addLevelSoundEvent(this, LEVEL_EVENT_MAP.get(this.recordItem.getId()));
        }
    }

    public void stop() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_STOP_RECORD);
    }

    public void dropItem() {
        if (this.recordItem.getId() != AIR) {
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
