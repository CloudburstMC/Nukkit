package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.nio.ByteOrder;

public class LevelEventGenericPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET;

    public int eventId;
    public CompoundTag tag;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, eventId);
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer)) {
            NBTIO.write(tag, stream, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
