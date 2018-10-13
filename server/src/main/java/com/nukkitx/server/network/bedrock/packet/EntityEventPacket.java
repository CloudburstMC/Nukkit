package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.entity.EntityEvent;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readRuntimeEntityId;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;
import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class EntityEventPacket implements BedrockPacket {
    private long runtimeEntityId;
    private EntityEvent event;
    private int data;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(event.ordinal());
        writeInt(buffer, data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        event = EntityEvent.byId(buffer.readByte());
        data = readInt(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
