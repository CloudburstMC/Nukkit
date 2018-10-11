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
public class AvailableEntityIdentifiersPacket implements BedrockPacket {
    private CompoundTag identifiers; // ListTag "idlist"
    /*
    ListTag "idlist"
        CompoundTag
            int ???
            String ???
            String ???
            boolean "hasspawnegg"
            boolean "summonable"
            boolean "experimental"

     */

    @Override
    public void encode(ByteBuf buffer) {
        try (NBTOutputStream writer = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.BEDROCK)) {
            writer.write(identifiers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
