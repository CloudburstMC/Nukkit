package cn.nukkit.network.protocol;

/**
 * Created by on 15-10-12.
 */
public class DisconnectPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.DISCONNECT_PACKET;

    public String message;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.message);
    }


}
