package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetDifficultyPacket extends DataPacket {

    public int difficulty;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_DIFFICULTY_PACKET;
    }

    @Override
    public void decode() {
        this.difficulty = (int) this.getUnsignedVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.difficulty);
    }
}
