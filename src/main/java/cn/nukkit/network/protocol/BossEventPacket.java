package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by CreeperFace on 30. 10. 2016.
 */
@ToString
public class BossEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

    /* S2C: Shows the bossbar to the player. */
    public static final int TYPE_SHOW = 0;
    /* C2S: Registers a player to a boss fight. */
    public static final int TYPE_REGISTER_PLAYER = 1;
    /* S2C: Removes the bossbar from the client. */
    public static final int TYPE_HIDE = 2;
    /* C2S: Unregisters a player from a boss fight. */
    public static final int TYPE_UNREGISTER_PLAYER = 3;
    /* S2C: Sets the bar percentage. */
    public static final int TYPE_HEALTH_PERCENT = 4;
    /* S2C: Sets title of the bar. */
    public static final int TYPE_TITLE = 5;
    /* S2C: Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this */
    public static final int TYPE_UNKNOWN_6 = 6;
    /* S2C: Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever. */
    public static final int TYPE_TEXTURE = 7;

    public long bossUniqueId;
    public int eventType;
    public long playerUniqueId;
    public float healthPercent;
    public String title = "";
    public int unknownLShort;
    public int color;
    public int overlay;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.bossUniqueId = this.getEntityUniqueId();
        this.eventType = (int) this.getUnsignedVarInt();
        switch (this.eventType) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
                this.playerUniqueId = this.getEntityUniqueId();
                break;
            case TYPE_SHOW:
                this.title = this.getString();
                this.healthPercent = this.getLFloat();
            case TYPE_UNKNOWN_6:
                this.unknownLShort = this.getLShort();
            case TYPE_TEXTURE:
                this.color = (int) this.getUnsignedVarInt();
                this.overlay = (int) this.getUnsignedVarInt();
                break;
            case TYPE_HEALTH_PERCENT:
                this.healthPercent = this.getLFloat();
                break;
            case TYPE_TITLE:
                this.title = this.getString();
                break;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.bossUniqueId);
        this.putUnsignedVarInt(this.eventType);
        switch (this.eventType) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
                this.putEntityUniqueId(this.playerUniqueId);
                break;
            case TYPE_SHOW:
                this.putString(this.title);
                this.putLFloat(this.healthPercent);
            case TYPE_UNKNOWN_6:
                this.putLShort(this.unknownLShort);
            case TYPE_TEXTURE:
                this.putUnsignedVarInt(this.color);
                this.putUnsignedVarInt(this.overlay);
                break;
            case TYPE_HEALTH_PERCENT:
                this.putLFloat(this.healthPercent);
                break;
            case TYPE_TITLE:
                this.putString(this.title);
                break;
        }
    }
}
