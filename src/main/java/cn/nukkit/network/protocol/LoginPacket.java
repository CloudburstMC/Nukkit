package cn.nukkit.network.protocol;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.LOGIN_PACKET;

    public String username;

    public int protocol1;
    public int protocol2;

    public long clientId;
    public UUID clientUUID;

    public String serverAddress;
    public String clientSecret;

    public boolean slim = false;
    public String skin = null;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        username = getString();
        protocol1 = getInt();
        protocol2 = getInt();
        if (protocol1 < Info.CURRENT_PROTOCOL) {
            setBuffer(null);
            setOffset(0);
            return;
        }
        clientId = getLong();
        clientUUID = getUUID();
        serverAddress = getString();
        clientSecret = getString();

        slim = getByte() > 0;
        skin = getString();
    }


    @Override
    public void encode() {

    }

}
