package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class CameraPacket extends DataPacket {

    public long cameraUniqueId;
    public long playerUniqueId;

    @Override
    public short pid() {
        return ProtocolInfo.CAMERA_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.cameraUniqueId = Binary.readVarLong(buffer);
        this.playerUniqueId = Binary.readVarLong(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.cameraUniqueId);
        Binary.writeEntityUniqueId(buffer, this.playerUniqueId);
    }
}
