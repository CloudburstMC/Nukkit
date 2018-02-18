package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetDifficultyPacket extends DataPacket {

    public int difficulty;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SET_DIFFICULTY_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.difficulty = (int) this.getUnsignedVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putUnsignedVarInt(this.difficulty);
    }

}
