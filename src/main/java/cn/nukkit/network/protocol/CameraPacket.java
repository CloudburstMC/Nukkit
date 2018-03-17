package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class CameraPacket extends DataPacket {

    public long cameraUniqueId;
    public long playerUniqueId;

    @Override
    public byte pid() {
        return ProtocolInfo.CAMERA_PACKET;
    }

    @Override
    public void decode() {
        this.cameraUniqueId = this.getVarLong();
        this.playerUniqueId = this.getVarLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.cameraUniqueId);
        this.putEntityUniqueId(this.playerUniqueId);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
