package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Jukebox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.item.RecordItem;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.item.ItemIds.*;
import static com.nukkitx.math.vector.Vector3i.UP;

/**
 * @author CreeperFace
 */
public class JukeboxBlockEntity extends BaseBlockEntity implements Jukebox {

    private static final Map<Identifier, SoundEvent> SOUND_MAP = new IdentityHashMap<>();

    static {
        SOUND_MAP.put(RECORD_13, SoundEvent.RECORD_13);
        SOUND_MAP.put(RECORD_CAT, SoundEvent.RECORD_CAT);
        SOUND_MAP.put(RECORD_BLOCKS, SoundEvent.RECORD_BLOCKS);
        SOUND_MAP.put(RECORD_CHIRP, SoundEvent.RECORD_CHIRP);
        SOUND_MAP.put(RECORD_FAR, SoundEvent.RECORD_FAR);
        SOUND_MAP.put(RECORD_MALL, SoundEvent.RECORD_MALL);
        SOUND_MAP.put(RECORD_MELLOHI, SoundEvent.RECORD_MELLOHI);
        SOUND_MAP.put(RECORD_STAL, SoundEvent.RECORD_STAL);
        SOUND_MAP.put(RECORD_STRAD, SoundEvent.RECORD_STRAD);
        SOUND_MAP.put(RECORD_WARD, SoundEvent.RECORD_WARD);
        SOUND_MAP.put(RECORD_11, SoundEvent.RECORD_11);
        SOUND_MAP.put(RECORD_WAIT, SoundEvent.RECORD_WAIT);
    }

    private Item recordItem;

    public JukeboxBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForCompound("RecordItem", itemTag -> {
            this.recordItem = ItemUtils.deserializeItem(itemTag);
        });
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        if (this.recordItem != null && !this.recordItem.isNull()) {
            tag.tag(ItemUtils.serializeItem(this.recordItem).toBuilder().build("RecordItem"));
        }
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.JUKEBOX;
    }

    public Item getRecordItem() {
        return recordItem;
    }

    public void setRecordItem(Item recordItem) {
        this.recordItem = recordItem;
    }

    public void play() {
        if (this.recordItem instanceof RecordItem) {
            this.getLevel().addLevelSoundEvent(this.getPosition(), SOUND_MAP.get(this.recordItem.getId()));
        }
    }

    public void stop() {
        this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.STOP_RECORD);
    }

    public void dropItem() {
        if (this.recordItem != null && !this.recordItem.isNull()) {
            this.stop();
            this.getLevel().dropItem(this.getPosition().add(UP), this.recordItem);
            this.recordItem = null;
        }
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
