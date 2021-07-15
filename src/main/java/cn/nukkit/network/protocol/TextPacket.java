package cn.nukkit.network.protocol;

import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class TextPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TEXT_PACKET;

    public Type type;
    public boolean isLocalized;
    public String source = "";
    public String message = "";
    public String[] parameters = new String[0];
    public String xboxUserId = "";
    public String platformChatId = "";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = Type.values()[this.getByte()];
        this.isLocalized = this.getBoolean() || this.type == Type.TRANSLATION;
        switch (this.type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                this.source = this.getString();
            case RAW:
            case TIP:
            case SYSTEM:
            case JSON:
            case JSON_WHISPER:
                this.message = this.getString();
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                this.message = this.getString();
                int count = (int) this.getUnsignedVarInt();
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
        this.putByte((byte) this.type.ordinal());
        this.putBoolean(this.isLocalized || this.type == Type.TRANSLATION);
        switch (this.type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT:
                this.putString(this.source);
            case RAW:
            case TIP:
            case SYSTEM:
            case JSON:
            case JSON_WHISPER:
                this.putString(this.message);
                break;
            case TRANSLATION:
            case POPUP:
            case JUKEBOX_POPUP:
                this.putString(this.message);
                this.putUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
        this.putString(this.xboxUserId);
        this.putString(this.platformChatId);
    }

    public static enum Type {

        RAW,
        CHAT,
        TRANSLATION,
        POPUP,
        JUKEBOX_POPUP,
        TIP,
        SYSTEM,
        WHISPER,
        ANNOUNCEMENT,
        JSON,
        JSON_WHISPER
    }
}
