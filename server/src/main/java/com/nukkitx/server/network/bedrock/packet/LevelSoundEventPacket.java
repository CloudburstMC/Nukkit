package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.SoundEvent;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readVector3f;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3f;
import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class LevelSoundEventPacket implements BedrockPacket {
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
        writeInt(buffer, extraData);
        writeInt(buffer, pitch);
        buffer.writeBoolean(unknown0);
        buffer.writeBoolean(relativeVolumeDisabled);
    }

    @Override
    public void decode(ByteBuf buffer) {
        soundEvent = SoundEvent.values()[buffer.readByte()];
        position = readVector3f(buffer);
        extraData = readInt(buffer);
        pitch = readInt(buffer);
        unknown0 = buffer.readBoolean();
        relativeVolumeDisabled = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
