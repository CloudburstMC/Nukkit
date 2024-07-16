package cn.nukkit.network.protocol;

import lombok.ToString;

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
    public static final byte TYPE_OBJECT = 9;
    public static final byte TYPE_OBJECT_WHISPER = 10;

    public byte type;
    public String source = "";
    public String message = "";
    public String[] parameters = new String[0];
    public boolean isLocalized = false;
    public String xboxUserId = "";
    public String platformChatId = "";
    public String filteredMessage = "";

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
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER:
                this.message = this.getString();
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.message = this.getString();
                int paramCount = (int) this.getUnsignedVarInt();
                this.parameters = new String[Math.min(paramCount, 128)];
                for (int i = 0; i < this.parameters.length; i++) {
                    this.parameters[i] = this.getString();
                }
        }
        this.xboxUserId = this.getString();
        this.platformChatId = this.getString();
        this.filteredMessage = this.getString();
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
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER:
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
        this.putString(this.filteredMessage);
    }
}
