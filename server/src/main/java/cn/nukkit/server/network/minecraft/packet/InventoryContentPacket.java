package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Arrays;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemInstance;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemInstance;

@Data
public class InventoryContentPacket implements MinecraftPacket {
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
