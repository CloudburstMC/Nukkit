package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readLEAsciiString;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;

@Data
@EqualsAndHashCode(exclude = {"skinData"})
@ToString(exclude = {"chainData", "skinData"})
public class LoginPacket implements BedrockPacket {
    private int protocolVersion;
    // Base64 strings so we only need Ascii characters.
    private AsciiString chainData;
    private AsciiString skinData;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        buffer.markReaderIndex();
        protocolVersion = buffer.readInt();
        if (protocolVersion > 65535) {
            buffer.resetReaderIndex();
            protocolVersion = buffer.readUnsignedShort();
        }

        ByteBuf jwt = buffer.readSlice(readUnsignedInt(buffer)); // Get the JWT.
        chainData = readLEAsciiString(jwt);
        skinData = readLEAsciiString(jwt);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
