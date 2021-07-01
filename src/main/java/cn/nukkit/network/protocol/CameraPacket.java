package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class CameraPacket extends DataPacket {

    public long cameraId;
    public long playerId;

    @Override
    public byte pid() {
        return ProtocolInfo.CAMERA_PACKET;
    }

    @Override
    public void decode() {
        this.cameraId = this.getEntityUniqueId();
        this.playerId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.cameraId);
        this.putEntityUniqueId(this.playerId);
    }
}
