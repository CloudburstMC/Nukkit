package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AdventureSettingsPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.ADVENTURE_SETTINGS_PACKET;

    public int flags;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putInt(flags);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
