package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemStack;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemStack;

@Data
public class InventoryContentPacket implements MinecraftPacket {
    private int windowId;
    private List<ItemStack> items = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, windowId);
        writeUnsignedInt(buffer, items.size());
        items.forEach(item -> writeItemStack(buffer, item));
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = readUnsignedInt(buffer);
        int itemSize = readUnsignedInt(buffer);

        for (int i = 0; i < itemSize; i++) {
            items.add(readItemStack(buffer));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
