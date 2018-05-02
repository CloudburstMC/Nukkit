package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.entity.Attribute;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writePlayerAttributes;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class UpdateAttributesPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private List<Attribute> attributes = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writePlayerAttributes(buffer, attributes);
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
