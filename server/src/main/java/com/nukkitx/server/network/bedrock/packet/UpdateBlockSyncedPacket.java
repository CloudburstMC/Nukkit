package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.server.network.util.VarInts.writeUnsignedLong;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateBlockSyncedPacket extends UpdateBlockPacket {
    private long runtimeEntityId;
    private long unknownLong1;

    @Override
    public void encode(ByteBuf buffer) {
        super.encode(buffer);
        writeUnsignedLong(buffer, runtimeEntityId);
        writeUnsignedLong(buffer, unknownLong1);
    }

    @Override
    public void decode(ByteBuf buffer) {
        super.decode(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}
