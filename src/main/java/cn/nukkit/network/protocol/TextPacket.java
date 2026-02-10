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
    public static final byte TYPE_OBJECT_WHISPER = 9;
    public static final byte TYPE_OBJECT = 10;
    public static final byte TYPE_OBJECT_ANNOUNCEMENT = 11;

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
        this.isLocalized = this.getBoolean() || type == TYPE_TRANSLATION;

        switch (this.getByte()) {
            case 0: // MessageOnly
                for (int i = 0; i < 6; i++) {
                    this.getString();
                }
                this.type = (byte) getByte();
                this.message = this.getString();
                break;
            case 1: // AuthorAndMessage
                for (int i = 0; i < 3; i++) {
                    this.getString();
                }
                this.type = (byte) getByte();
                this.source = this.getString();
                this.message = this.getString();
                break;
            case 2: // MessageAndParams
                for (int i = 0; i < 3; i++) {
                    this.getString();
                }
                this.type = (byte) getByte();
                this.message = this.getString();
                int paramCount = (int) this.getUnsignedVarInt();
                if (paramCount > 4) {
                    throw new IllegalArgumentException("Parameter List maxItems is 4");
                }
                this.parameters = new String[paramCount];
                for (int i = 0; i < this.parameters.length; i++) {
                    this.parameters[i] = this.getString();
                }
                break;
            default:
                throw new IllegalArgumentException("Not oneOf<MessageOnly, AuthorAndMessage, MessageAndParams>");
        }

        this.xboxUserId = this.getString();
        this.platformChatId = this.getString();

        if (this.getBoolean()) {
            this.filteredMessage = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();

        this.putBoolean(this.isLocalized || type == TYPE_TRANSLATION);

        // 1.21.130 doesn't allow empty messages
        if (this.message.isEmpty()) {
            this.message = " ";
        }

        switch (this.type) {
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER:
            case TYPE_OBJECT_ANNOUNCEMENT:
                this.putByte((byte) 0); // MessageOnly
                this.putString("raw");
                this.putString("tip");
                this.putString("systemMessage");
                this.putString("textObjectWhisper");
                this.putString("textObjectAnnouncement");
                this.putString("textObject");
                this.putByte(this.type);
                this.putString(this.message);
                break;

            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.putByte((byte) 1); // AuthorAndMessage
                this.putString("chat");
                this.putString("whisper");
                this.putString("announcement");
                this.putByte(this.type);
                this.putString(this.source);
                this.putString(this.message);
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.putByte((byte) 2); // MessageAndParams
                this.putString("translate");
                this.putString("popup");
                this.putString("jukeboxPopup");
                this.putByte(this.type);
                this.putString(this.message);
                this.putUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }

        this.putString(this.xboxUserId);
        this.putString(this.platformChatId);

        this.putBoolean(!this.filteredMessage.isEmpty());
        if (!this.filteredMessage.isEmpty()) {
            this.putString(this.filteredMessage);
        }
    }
}
