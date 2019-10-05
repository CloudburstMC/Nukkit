package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.nio.charset.StandardCharsets;


/**
 * Created by on 15-10-13.
 */
@ToString(exclude = {"skinData", "chainData"})
public class LoginPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public int protocol;

    public String chainData;
    public String skinData;

    private static String readString(ByteBuf buffer) {
        int length = buffer.readIntLE();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.US_ASCII); // base 64 encoded.
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.protocol = buffer.readInt();
        if (protocol == 0) {
            buffer.skipBytes(2);
            this.protocol = buffer.readInt();
        }
        ByteBuf jwt = Binary.readVarIntBuffer(buffer);
        this.chainData = readString(jwt);
        this.skinData = readString(jwt);
    }

    public int getProtocol() {
        return protocol;
    }

    @Override
    protected void encode(ByteBuf buffer) {
    }
}
