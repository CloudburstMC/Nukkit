package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class TextPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TEXT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte TYPE_RAW = 0;
    public static final byte TYPE_CHAT = 1;
    public static final byte TYPE_TRANSLATION = 2;
    public static final byte TYPE_POPUP = 3;
    public static final byte TYPE_JUKEBOX_POPUP = 4;
    public static final byte TYPE_TIP = 5;
    public static final byte TYPE_SYSTEM = 6;
    public static final byte TYPE_WHISPER = 7;
    public static final byte TYPE_ANNOUNCEMENT = 8;
    public static final byte TYPE_JSON = 9;

    public byte type;
    public String source = "";
    public String message = "";
    public String[] parameters = new String[0];
    public boolean isLocalized = false;
    public String xboxUserId = "";
    public String platformChatId = "";

    @Override
    public void decode() {
        this.type = (byte) getByte();
        this.isLocalized = this.getBoolean() || type == TYPE_TRANSLATION;
        switch (type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.source = this.getString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_JSON:
                this.message = this.getString();
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.message = this.getString();
                int count = (int) this.getUnsignedVarInt();
                this.parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    this.parameters[i] = this.getString();
                }
        }
        this.xboxUserId = this.getString();
        this.platformChatId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putBoolean(this.isLocalized || type == TYPE_TRANSLATION);
        switch (this.type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.putString(this.source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_JSON:
                this.putString(this.message);
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.putString(this.message);
                this.putUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
        this.putString(this.xboxUserId);
        this.putString(this.platformChatId);
    }
}
