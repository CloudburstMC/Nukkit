package cn.nukkit.network.protocol;

import java.util.UUID;

/**
 * Created by on 15-10-13.
 */
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.LOGIN_PACKET;

    public String userName;

    public byte protocolVersion;
    public byte protocol;

    public long clientId;
    public UUID clientUUID;

    public String serverAddress;
    public String clientSecret;

    public boolean slim;
    public String skin;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        userName = getString();
        protocolVersion = getByte();
        protocol = getByte();
        if (protocolVersion < Info.CURRENT_PROTOCOL) try {
            throw new RuntimeException("Outdated protocol version, current is " + Info.CURRENT_PROTOCOL + " but get " + protocolVersion + '!');
        } finally {
            setBuffer(null, 0);
        }
        clientId = getLong();
        clientUUID = getUUID();
        serverAddress = getString();
        clientSecret = getString();

        slim = getByte() > 0;
        skin = getString();
    }


    @Override
    public void decode() {
        ;
    }

}
