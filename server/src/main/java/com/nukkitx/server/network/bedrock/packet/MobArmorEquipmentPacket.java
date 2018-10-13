package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;

@Data
public class MobArmorEquipmentPacket implements BedrockPacket {
    private long runtimeEntityId;
    private ItemInstance helmet;
    private ItemInstance chestplate;
    private ItemInstance leggings;
    private ItemInstance boots;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemInstance(buffer, helmet);
        writeItemInstance(buffer, chestplate);
        writeItemInstance(buffer, leggings);
        writeItemInstance(buffer, boots);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        helmet = readItemInstance(buffer);
        chestplate = readItemInstance(buffer);
        leggings = readItemInstance(buffer);
        boots = readItemInstance(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
