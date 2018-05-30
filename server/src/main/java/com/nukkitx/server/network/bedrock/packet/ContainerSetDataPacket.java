package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class ContainerSetDataPacket implements BedrockPacket {
    private byte inventoryId;
    private int property; //TODO: Add property type enum.
    private int value;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(inventoryId);
        writeInt(buffer, property);
        writeInt(buffer, value);
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
