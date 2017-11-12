package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetDifficultyPacket extends DataPacket {

    public int difficulty;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.difficulty = (int) this.getUnsignedVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putUnsignedVarInt(this.difficulty);
    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.SET_DIFFICULTY_PACKET :
                ProtocolInfo.SET_DIFFICULTY_PACKET;
    }

}
