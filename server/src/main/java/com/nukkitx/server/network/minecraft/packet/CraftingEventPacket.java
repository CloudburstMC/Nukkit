package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readItemInstance;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readUuid;

@Data
public class CraftingEventPacket implements MinecraftPacket {
    private final List<ItemInstance> input = new ArrayList<>();
    private final List<ItemInstance> output = new ArrayList<>();
    private byte windowId;
    private Type type;
    private UUID uuid;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        type = Type.values()[readSignedInt(buffer)];
        uuid = readUuid(buffer);

        int inputCount = readUnsignedInt(buffer);
        for (int i = 0; i < inputCount; i++) {
            input.add(readItemInstance(buffer));
        }

        int outputCount = readUnsignedInt(buffer);
        for (int i = 0; i < outputCount; i++) {
            output.add(readItemInstance(buffer));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Type {
        SHAPELESS,
        SHAPED,
        FURNACE,
        FURNACE_DATA,
        MULTI,
        SHULKER_BOX
    }
}
