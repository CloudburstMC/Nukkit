package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.nbt.NBTEncodingType;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.Tag;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class UpdateTradePacket implements BedrockPacket {
    private byte windowId;
    private byte windowType;
    private int unknown0; // Couldn't find anything on this one.
    private int unknown1; // Something to do with AI and randomness?
    private boolean willing;
    private long traderUniqueEntityId;
    private long playerUniqueEntityId;
    private String displayName;
    private Tag<?> offers;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(windowType);
        writeInt(buffer, unknown0);
        writeInt(buffer, unknown1);
        buffer.writeBoolean(willing);
        writeUniqueEntityId(buffer, traderUniqueEntityId);
        writeUniqueEntityId(buffer, playerUniqueEntityId);
        writeString(buffer, displayName);
        try (NBTOutputStream writer = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.BEDROCK)) {
            writer.write(offers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
