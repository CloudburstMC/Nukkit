package cn.nukkit.server.network.protocol;

/**
 * @author Tee7even
 */
public class SetTitlePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    public TitleType type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = TitleType.values()[this.getVarInt()];
        this.text = this.getString();
        this.fadeInTime = this.getVarInt();
        this.stayTime = this.getVarInt();
        this.fadeOutTime = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(type.ordinal());
        this.putString(text);
        this.putVarInt(fadeInTime);
        this.putVarInt(stayTime);
        this.putVarInt(fadeOutTime);
    }

    public enum TitleType {
        CLEAR,
        RESET,
        TITLE,
        SUBTITLE,
        ACTION_BAR,
        ANIMATION_TIMES
    }
}
