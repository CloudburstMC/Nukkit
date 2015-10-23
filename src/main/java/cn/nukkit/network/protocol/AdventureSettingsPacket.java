package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AdventureSettingsPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.ADVENTURE_SETTINGS_PACKET;

    public int flag;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putInt(flag);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
