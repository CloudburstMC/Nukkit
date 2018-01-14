package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemStack;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemStack;

@Data
public class InventorySlotPacket implements MinecraftPacket {
    private int windowId;
    private int inventorySlot;
    private ItemStack slot;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, windowId);
        writeUnsignedInt(buffer, inventorySlot);
        writeItemStack(buffer, slot);
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = readUnsignedInt(buffer);
        inventorySlot = readUnsignedInt(buffer);
        slot = readItemStack(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
