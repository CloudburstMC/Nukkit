package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@Data
public class PlayerHotbarPacket implements MinecraftPacket {
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
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
