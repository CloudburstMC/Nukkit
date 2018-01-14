package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@Data
public class ContainerSetDataPacket implements MinecraftPacket {
    private byte inventoryId;
    private int property; //TODO: Add property type enum.
    private int value;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(inventoryId);
        writeSignedInt(buffer, property);
        writeSignedInt(buffer, value);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
