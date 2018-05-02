package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.nbt.util.VarInt;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddHangingEntityPacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Vector3i blockPosition;
    private int rotation;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3i(buffer, blockPosition);
        VarInt.writeSignedInt(buffer, rotation);
    }

    @Override
    public void decode(ByteBuf buffer) {
        uniqueEntityId = readUniqueEntityId(buffer);
        runtimeEntityId = readRuntimeEntityId(buffer);
        blockPosition = readVector3i(buffer);
        rotation = VarInt.readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
