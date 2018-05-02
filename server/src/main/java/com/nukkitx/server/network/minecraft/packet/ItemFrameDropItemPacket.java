package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.readVector3i;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class ItemFrameDropItemPacket implements MinecraftPacket {
    private Vector3i blockPosition;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPosition = readVector3i(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
