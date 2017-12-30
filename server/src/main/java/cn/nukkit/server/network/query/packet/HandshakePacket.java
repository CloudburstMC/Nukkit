package cn.nukkit.server.network.query.packet;

import cn.nukkit.server.network.query.QueryPacket;
import cn.nukkit.server.network.query.QueryUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HandshakePacket implements QueryPacket {
    private static final short ID = 0x09;
    // Both
    private int sessionId;
    // Response
    private String token;

    @Override
    public void decode(ByteBuf buffer) {
        sessionId = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(sessionId);
        QueryUtil.writeNullTerminatedString(buffer, token);
    }

    @Override
    public short getId() {
        return ID;
    }
}
