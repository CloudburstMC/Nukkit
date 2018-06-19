package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;

@Data
public class NpcRequestPacket implements BedrockPacket {
    private long runtimeEntityId;
    private Type requestType;
    private String command;
    private byte actionType;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(requestType.ordinal());
        writeString(buffer, command);
        buffer.writeByte(actionType);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        // TODO: Didn't really look too far into this.
        SET_ACTION,
        EXECUTE_COMMAND_ACTION,
        EXECUTE_CLOSING_COMMANDS
    }
}
