package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetDifficultyPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.SET_DIFFICULTY_PACKET;

    public int difficulty;

    @Override
    protected void decode(ByteBuf buffer) {
        this.difficulty = (int) Binary.readUnsignedVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.difficulty);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
