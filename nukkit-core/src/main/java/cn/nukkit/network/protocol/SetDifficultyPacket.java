package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetDifficultyPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_DIFFICULTY_PACKET;

    public int difficulty;

    @Override
    public void decode() {
        difficulty = getInt();
    }

    @Override
    public void encode() {
        reset();
        putInt(difficulty);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
