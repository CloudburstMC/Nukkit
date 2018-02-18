package cn.nukkit.network.protocol;

public class CameraPacket extends DataPacket {

    public long cameraUniqueId;
    public long playerUniqueId;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("CAMERA_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.cameraUniqueId = this.getVarLong();
        this.playerUniqueId = this.getVarLong();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityUniqueId(this.cameraUniqueId);
        this.putEntityUniqueId(this.playerUniqueId);
    }
}
