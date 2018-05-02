package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class PlaySoundPacket implements MinecraftPacket {
    private String sound;
    private Vector3i blockPosition;
    private float volume;
    private float pitch;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, sound);
        writeVector3i(buffer, blockPosition);
        buffer.writeFloatLE(volume);
        buffer.writeFloatLE(pitch);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
