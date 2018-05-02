package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.entity.EntityEvent;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readRuntimeEntityId;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class EntityEventPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private EntityEvent event;
    private int data;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(event.ordinal());
        writeSignedInt(buffer, data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        event = EntityEvent.byId(buffer.readByte());
        data = readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
