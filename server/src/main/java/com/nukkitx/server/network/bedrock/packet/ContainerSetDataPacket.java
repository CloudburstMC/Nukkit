package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class ContainerSetDataPacket implements BedrockPacket {
    private byte inventoryId;
    private Property property;
    private int value;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(inventoryId);
        writeInt(buffer, property.getId());
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

    @AllArgsConstructor
    @Getter
    public enum Property {
        FURNACE_TICK_COUNT(0),
        FURNACE_LIT_TIME(1),
        FURNACE_LIT_DURATION(2),
        //unknown
        FURNACE_FUEL_AUX(4),

        BREWING_STAND_BREW_TIME(0),
        BREWING_STAND_FUEL_AMOUNT(1),
        BREWING_STAND_FUEL_TOTAL(2);

        private final int id;
    }
}
