package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Tee7even
 */
@ToString
public class SetTitlePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    public Type type;
    public String text = "";
    public int fadeInTime;
    public int stayTime;
    public int fadeOutTime;
    public String xuid = "";
    public String platformOnlineId = "";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = Type.values()[this.getVarInt()];
        this.text = this.getString();
        this.fadeInTime = this.getVarInt();
        this.stayTime = this.getVarInt();
        this.fadeOutTime = this.getVarInt();
        this.xuid = this.getString();
        this.platformOnlineId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type.ordinal());
        this.putString(this.text);
        this.putVarInt(this.fadeInTime);
        this.putVarInt(this.stayTime);
        this.putVarInt(this.fadeOutTime);
        this.putString(this.xuid);
        this.putString(this.platformOnlineId);
    }

    public static enum Type {

        CLEAR_TITLE,
        RESET_TITLE,
        SET_TITLE,
        SET_SUBTITLE,
        SET_ACTIONBAR_MESSAGE,
        SET_ANIMATION_TIMES,
        SET_TITLE_JSON,
        SET_SUBTITLE_JSON,
        SET_ACTIONBAR_MESSAGE_JSON
    }
}
