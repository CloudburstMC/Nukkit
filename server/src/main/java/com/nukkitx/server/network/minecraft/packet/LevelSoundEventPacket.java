package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.SoundEvent;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readVector3f;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeVector3f;

@Data
public class LevelSoundEventPacket implements MinecraftPacket {
    private SoundEvent soundEvent;
    private Vector3f position;
    private int extraData;
    private int pitch;
    private boolean unknown0;
    private boolean relativeVolumeDisabled;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(soundEvent.ordinal());
        writeVector3f(buffer, position);
        writeSignedInt(buffer, extraData);
        writeSignedInt(buffer, pitch);
        buffer.writeBoolean(unknown0);
        buffer.writeBoolean(relativeVolumeDisabled);
    }

    @Override
    public void decode(ByteBuf buffer) {
        soundEvent = SoundEvent.values()[buffer.readByte()];
        position = readVector3f(buffer);
        extraData = readSignedInt(buffer);
        pitch = readSignedInt(buffer);
        unknown0 = buffer.readBoolean();
        relativeVolumeDisabled = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
