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

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class UpdateEquipPacket implements BedrockPacket {
    private byte windowId;
    private byte windowType;
    private int unknown0; // Couldn't find anything on this one. Looks like it isn't used?
    private long uniqueEntityId;
    private Tag<?> tag;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(windowType);
        writeInt(buffer, unknown0);
        writeUniqueEntityId(buffer, uniqueEntityId);
        try (NBTOutputStream writer = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.BEDROCK)) {
            writer.write(tag);
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
