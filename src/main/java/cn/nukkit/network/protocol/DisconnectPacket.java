package cn.nukkit.network.protocol;

/**
 * Created by on 15-10-12.
 */
public class DisconnectPacket extends DataPacket {

    private String message;

    public DisconnectPacket(String message) {
        this.message = message;
    }

    @Override
    public byte pid() {
        return Info.DISCONNECT_PACKET;
    }

    @Override
    public void encode() {
        reset();
        putString(message);
    }

    @Override
    public void decode() {
        message = getString();
    }

}
