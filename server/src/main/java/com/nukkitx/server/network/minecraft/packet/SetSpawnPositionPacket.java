package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class SetSpawnPositionPacket implements MinecraftPacket {
    private Type spawnType;
    private Vector3i blockPosition;
    private boolean spawnForced;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, spawnType.ordinal());
        writeVector3i(buffer, blockPosition);
        buffer.writeBoolean(spawnForced);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }
}
