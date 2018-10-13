package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class PlayerHotbarPacket implements BedrockPacket {
    private final List<ItemInstance> items = new ArrayList<>();
    private int selectedHotbarSlot;
    private byte windowId;
    private boolean selectHotbarSlot;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, selectedHotbarSlot);
        buffer.writeByte(windowId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        selectedHotbarSlot = readUnsignedInt(buffer);
        windowId = buffer.readByte();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
