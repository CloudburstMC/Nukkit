package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class MobArmorEquipmentPacket implements MinecraftPacket {
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
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
