package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.data.CommandOriginData;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;

@Data
public class CommandRequestPacket implements BedrockPacket {
    private String command;
    private CommandOriginData commandOriginData;
    private boolean internal;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, command);
        writeCommandOriginData(buffer, commandOriginData);
        buffer.writeBoolean(internal);
    }

    @Override
    public void decode(ByteBuf buffer) {
        command = readString(buffer);
        commandOriginData = readCommandOriginData(buffer);
        internal = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
