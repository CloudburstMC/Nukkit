package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class UnknownPacket implements MinecraftPacket {
    private short id;
    private ByteBuf payload;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(id);
        buffer.writeBytes(payload);
    }

    @Override
    public void decode(ByteBuf buffer) {
        id = buffer.readUnsignedByte();
        payload = buffer.readBytes(buffer.readableBytes());
    }

    @Override
    public String toString() {
        return "UNKNOWN - " + Integer.toHexString(id) + " - Hex: " + ByteBufUtil.hexDump(payload);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        if (log.isDebugEnabled()) {
            log.debug("Unknown packet received with ID " + Integer.toHexString(id));
            log.debug("Dump: {}", ByteBufUtil.hexDump(payload));
        }
        payload.release();
    }
}
