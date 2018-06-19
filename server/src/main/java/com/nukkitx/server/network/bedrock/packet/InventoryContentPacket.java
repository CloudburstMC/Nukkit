package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Arrays;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readItemInstance;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeItemInstance;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class InventoryContentPacket implements BedrockPacket {
    private int windowId;
    private ItemInstance[] items;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, windowId);
        writeUnsignedInt(buffer, items.length);
        for (ItemInstance item : items) {
            writeItemInstance(buffer, item);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = readUnsignedInt(buffer);
        int itemSize = readUnsignedInt(buffer);
        items = new ItemInstance[itemSize];
        for (int i = 0; i < itemSize; i++) {
            items[i] = readItemInstance(buffer);
        }
    }

    public void setItems(ItemInstance[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
