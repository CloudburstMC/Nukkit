package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class MobArmorEquipmentPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemStack(buffer, helmet);
        writeItemStack(buffer, chestplate);
        writeItemStack(buffer, leggings);
        writeItemStack(buffer, boots);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        helmet = readItemStack(buffer);
        chestplate = readItemStack(buffer);
        leggings = readItemStack(buffer);
        boots = readItemStack(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
