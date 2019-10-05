package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public short pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        mapId = Binary.readEntityUniqueId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }
}
