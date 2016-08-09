package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AdventureSettingsPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADVENTURE_SETTINGS_PACKET;

    public int flags;
    public int userPermission;
    public int globalPermission;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putInt(flags);
        putInt(userPermission);
        putInt(globalPermission);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
