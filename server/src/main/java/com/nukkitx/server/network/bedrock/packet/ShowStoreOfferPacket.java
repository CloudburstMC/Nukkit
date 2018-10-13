package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;

@Data
public class ShowStoreOfferPacket implements BedrockPacket {
    private String offerId;
    private boolean shownToAll;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, offerId);
        buffer.writeBoolean(shownToAll);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound.
    }
}
