package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class BlockEventPacket implements MinecraftPacket {
    private Vector3i blockPosition;
    private Type eventType;
    private int eventData;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeSignedInt(buffer, eventType.ordinal());
        writeSignedInt(buffer, eventData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }

    public enum Type {
        NONE,
        CHEST
    }
}
