package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class TextPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.TEXT_PACKET;

    @Override
    public short pid() {
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
    protected void decode(ByteBuf buffer) {
        this.type = buffer.readByte();
        this.isLocalized = buffer.readBoolean() || type == TYPE_TRANSLATION;
        switch (type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.source = Binary.readString(buffer);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_JSON:
                this.message = Binary.readString(buffer);
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.message = Binary.readString(buffer);
                int count = (int) Binary.readUnsignedVarInt(buffer);
                this.parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    this.parameters[i] = Binary.readString(buffer);
                }
        }
        this.xboxUserId = Binary.readString(buffer);
        this.platformChatId = Binary.readString(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(this.type);
        buffer.writeBoolean(this.isLocalized || type == TYPE_TRANSLATION);
        switch (this.type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                Binary.writeString(buffer, this.source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_JSON:
                Binary.writeString(buffer, this.message);
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                Binary.writeString(buffer, this.message);
                Binary.writeUnsignedVarInt(buffer, this.parameters.length);
                for (String parameter : this.parameters) {
                    Binary.writeString(buffer, parameter);
                }
        }
        Binary.writeString(buffer, this.xboxUserId);
        Binary.writeString(buffer, this.platformChatId);
    }
}
