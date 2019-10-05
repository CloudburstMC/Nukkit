package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Tee7even
 */
@ToString
public class SetTitlePacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    public static final int TYPE_CLEAR = 0;
    public static final int TYPE_RESET = 1;
    public static final int TYPE_TITLE = 2;
    public static final int TYPE_SUBTITLE = 3;
    public static final int TYPE_ACTION_BAR = 4;
    public static final int TYPE_ANIMATION_TIMES = 5;

    public int type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.type = Binary.readVarInt(buffer);
        this.text = Binary.readString(buffer);
        this.fadeInTime = Binary.readVarInt(buffer);
        this.stayTime = Binary.readVarInt(buffer);
        this.fadeOutTime = Binary.readVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, type);
        Binary.writeString(buffer, text);
        Binary.writeVarInt(buffer, fadeInTime);
        Binary.writeVarInt(buffer, stayTime);
        Binary.writeVarInt(buffer, fadeOutTime);
    }
}
