package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class ModalFormRequestPacket implements BedrockPacket {
    private int formId;
    private String formData;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, formId);
        writeString(buffer, formData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        //
    }
}
