package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Noteblock;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

public class MusicBlockEntity extends BaseBlockEntity implements Noteblock {

    private byte note;
    private boolean powered;

    public MusicBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForByte("note", this::setNote);
        tag.listenForBoolean("powered", this::setPowered);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.byteTag("note", this.getNote());
        tag.booleanTag("powered", this.isPowered());
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.NOTEBLOCK;
    }

    public byte getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = (byte) note;
    }

    public void changeNote() {
        this.setNote((note + 1) % 25);
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }
}
