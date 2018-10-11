package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.SoundEvent;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import io.netty.buffer.ByteBuf;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

public class LevelSoundEvent2Packet implements BedrockPacket {
    private SoundEvent event;
    private Vector3f position;
    private int extraData;
    private String identifier;
    private boolean babySound;
    private boolean relativeVolumeDisabled;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(event.ordinal());
        writeVector3f(buffer, position);
        writeInt(buffer, extraData);
        writeString(buffer, identifier);
        buffer.writeBoolean(babySound);
        buffer.writeBoolean(relativeVolumeDisabled);
    }

    @Override
    public void decode(ByteBuf buffer) {
        event = SoundEvent.values()[buffer.readByte()];
        position = readVector3f(buffer);
        extraData = readInt(buffer);
        identifier = readString(buffer);
        babySound = buffer.readBoolean();
        relativeVolumeDisabled = buffer.readBoolean();
    }
}
