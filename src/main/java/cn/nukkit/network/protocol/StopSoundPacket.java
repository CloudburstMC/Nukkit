package cn.nukkit.network.protocol;

public class StopSoundPacket extends DataPacket {

    public String name;
    public boolean stopAll;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.STOP_SOUND_PACKET :
                ProtocolInfo.STOP_SOUND_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(this.name);
        this.putBoolean(this.stopAll);
    }
}
