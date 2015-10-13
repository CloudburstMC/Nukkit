package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class TextPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.TEXT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

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
        this.type = getByte();
        switch (type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                this.source = this.getString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.message = this.getString();
                break;

            case TYPE_TRANSLATION:
                this.message = this.getString();
                byte count = this.getByte();
                parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    parameters[i] = getString();
                }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        switch (this.type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                this.putString(this.source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.putString(this.message);
                break;

            case TYPE_TRANSLATION:
                this.putString(this.message);
                this.putByte((byte) this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
    }

}
