package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class TextPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.TEXT_PACKET;

    public static final byte TYPE_RAW = 0;
    public static final byte TYPE_CHAT = 1;
    public static final byte TYPE_TRANSLATION = 2;
    public static final byte TYPE_POPUP = 3;
    public static final byte TYPE_TIP = 4;
    public static final byte TYPE_SYSTEM = 5;

    public byte type;
    public String source;
    public String message;
    public String[] parameters;

    @Override
    public void decode() {
        type = getByte();
        switch (type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                source = getString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                message = getString();
                break;

            case TYPE_TRANSLATION:
                message = getString();
                byte count = getByte();
                parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    parameters[i] = getString();
                }
        }
    }

    @Override
    public void encode() {
        reset();
        putByte(type);
        switch (type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                putString(source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                putString(message);
                break;

            case TYPE_TRANSLATION:
                putString(message);
                putByte((byte) parameters.length);
                for (String parameter : parameters) {
                    putString(parameter);
                }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
