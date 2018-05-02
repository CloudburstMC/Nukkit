package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import com.nukkitx.server.network.minecraft.util.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddItemEntityPacket implements MinecraftPacket {
    private final MetadataDictionary metadata = new MetadataDictionary();
    private long uniqueEntityId;
    private long runtimeEntityId;
    private ItemInstance itemInstance;
    private Vector3f position;
    private Vector3f motion;
    private boolean fishing;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemInstance(buffer, itemInstance);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        buffer.writeBoolean(fishing);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
