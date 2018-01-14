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
public class PlayerHotbarPacket implements MinecraftPacket {
    private final List<ItemStack> items = new ArrayList<>();
    private int selectedHotbarSlot;
    private byte windowId;
    private boolean selectHotbarSlot;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, selectedHotbarSlot);
        buffer.writeByte(windowId);
        writeUnsignedInt(buffer, items.size());
        items.forEach(itemStack -> writeItemStack(buffer, itemStack));
    }

    @Override
    public void decode(ByteBuf buffer) {
        selectedHotbarSlot = readUnsignedInt(buffer);
        windowId = buffer.readByte();
        int itemCount = readUnsignedInt(buffer);
        for (int i = 0; i < itemCount; i++) {
            items.add(readItemStack(buffer));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
