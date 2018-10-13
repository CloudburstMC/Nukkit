package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import com.nukkitx.server.network.bedrock.data.CommandOriginData;
import com.nukkitx.server.network.bedrock.data.CommandOutputMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeCommandOriginData;

@Data
public class CommandOutputPacket implements BedrockPacket {
    private final List<CommandOutputMessage> outputMessages = new ArrayList<>();
    private CommandOriginData commandOriginData;
    private byte outputType;
    private int successCount;

    @Override
    public void encode(ByteBuf buffer) {
        writeCommandOriginData(buffer, commandOriginData);
        buffer.writeByte(outputType);

    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // This packet isn't handled
    }
}
