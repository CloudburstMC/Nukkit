package cn.nukkit.server.network.query;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class QueryUtil {
    public static final byte[] LONG_RESPONSE_PADDING_TOP = new byte[]{115, 112, 108, 105, 116, 110, 117, 109, 0, -128, 0};
    public static final byte[] LONG_RESPONSE_PADDING_BOTTOM = new byte[]{1, 112, 108, 97, 121, 101, 114, 95, 0, 0};

    public static void writeNullTerminatedByteArray(ByteBuf buf, byte[] array) {
        if (array != null) {
            buf.writeBytes(array);
        }
        buf.writeByte((byte) 0x00);
    }

    public static String readNullTerminatedString(ByteBuf in) {
        StringBuilder read = new StringBuilder();
        byte readIn;
        while ((readIn = in.readByte()) != '\0') {
            read.append((char) readIn);
        }
        return read.toString();
    }

    public static void writeNullTerminatedString(ByteBuf buf, String string) {
        writeNullTerminatedByteArray(buf, string.getBytes(StandardCharsets.UTF_8));
    }
}
