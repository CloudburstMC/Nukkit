package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.nbt.NBTEncodingType;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

@Data
public class BiomeDefinitionListPacket implements BedrockPacket {
    private CompoundTag tag;

    @Override
    public void encode(ByteBuf byteBuf) {
        try (NBTOutputStream nbtOutputStream = new NBTOutputStream(new LittleEndianByteBufOutputStream(byteBuf), NBTEncodingType.BEDROCK)) {
            nbtOutputStream.write(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }
}
