package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readString;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;

@Data
public class PurchaseReceiptPacket implements BedrockPacket {
    private final List<String> receipts = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        int receiptCount = readUnsignedInt(buffer);

        for (int i = 0; i < receiptCount; i++) {
            receipts.add(readString(buffer));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
