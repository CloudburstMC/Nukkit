package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class MobEquipmentPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private ItemStack item;
    private byte inventorySlot;
    private byte hotbarSlot;
    private byte windowId;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemStack(buffer, item);
        buffer.writeByte(inventorySlot);
        buffer.writeByte(hotbarSlot);
        buffer.writeByte(windowId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        item = readItemStack(buffer);
        inventorySlot = buffer.readByte();
        hotbarSlot = buffer.readByte();
        windowId = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
