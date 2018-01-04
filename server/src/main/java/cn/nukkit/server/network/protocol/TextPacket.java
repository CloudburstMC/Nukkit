package cn.nukkit.server.network.protocol;

/**
 * Created on 15-10-13.
 */
public class TextPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TEXT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public TextType type;
    public String source = "";
    public String message = "";
    public String[] parameters = new String[0];
    public boolean isLocalized = false;

    @Override
    public void decode() {
        this.type = TextType.values()[getByte()];
        this.isLocalized = this.getBoolean();
        switch (type) {
            case POPUP:
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                this.source = this.getString();
            case RAW:
            case TIP:
            case SYSTEM:
                this.message = this.getString();
                break;

            case TRANSLATION:
                this.message = this.getString();
                int count = (int) this.getUnsignedVarInt();
                this.parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    this.parameters[i] = this.getString();
                }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.type.ordinal());
        this.putBoolean(this.isLocalized);
        switch (this.type) {
            case POPUP:
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                this.putString(this.source);
            case RAW:
            case TIP:
            case SYSTEM:
                this.putString(this.message);
                break;

            case TRANSLATION:
                this.putString(this.message);
                this.putUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
    }

    public enum TextType {
        RAW,
        CHAT,
        TRANSLATION,
        POPUP,
        TIP,
        SYSTEM,
        WHISPER,
        ANNOUNCEMENT
    }
}
