package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created by CreeperFace on 30. 10. 2016.
 */
@ToString
public class BossEventPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

    /* S2C: Shows the bossbar to the player. */
    public static final int TYPE_SHOW = 0;
    /* C2S: Registers a player to a boss fight. */
    public static final int TYPE_REGISTER_PLAYER = 1;
    public static final int TYPE_UPDATE = 1;
    /* S2C: Removes the bossbar from the client. */
    public static final int TYPE_HIDE = 2;
    /* C2S: Unregisters a player from a boss fight. */
    public static final int TYPE_UNREGISTER_PLAYER = 3;
    /* S2C: Appears not to be implemented. Currently bar percentage only appears to change in response to the target entity's health. */
    public static final int TYPE_HEALTH_PERCENT = 4;
    /* S2C: Also appears to not be implemented. Title clientside sticks as the target entity's nametag, or their entity type name if not set. */
    public static final int TYPE_TITLE = 5;
    /* S2C: Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this */
    public static final int TYPE_UNKNOWN_6 = 6;
    /* S2C: Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever. */
    public static final int TYPE_TEXTURE = 7;

    public long bossEid;
    public int type;
    public long playerEid;
    public float healthPercent;
    public String title = "";
    public short unknown;
    public int color;
    public int overlay;
    
    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.bossEid = Binary.readEntityUniqueId(buffer);
        this.type = (int) Binary.readUnsignedVarInt(buffer);
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
                this.playerEid = Binary.readEntityUniqueId(buffer);
                break;
            case TYPE_SHOW:
                this.title = Binary.readString(buffer);
                this.healthPercent = buffer.readFloatLE();
            case TYPE_UNKNOWN_6:
                this.unknown = buffer.readShortLE();
            case TYPE_TEXTURE:
                this.color = (int) Binary.readUnsignedVarInt(buffer);
                this.overlay = (int) Binary.readUnsignedVarInt(buffer);
                break;
            case TYPE_HEALTH_PERCENT:
                this.healthPercent = buffer.readFloatLE();
                break;
            case TYPE_TITLE:
                this.title = Binary.readString(buffer);
                break;
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.bossEid);
        Binary.writeUnsignedVarInt(buffer, this.type);
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
                Binary.writeEntityUniqueId(buffer, this.playerEid);
                break;
            case TYPE_SHOW:
                Binary.writeString(buffer, this.title);
                buffer.writeFloatLE(this.healthPercent);
            case TYPE_UNKNOWN_6:
                buffer.writeShortLE(this.unknown);
            case TYPE_TEXTURE:
                Binary.writeUnsignedVarInt(buffer, this.color);
                Binary.writeUnsignedVarInt(buffer, this.overlay);
                break;
            case TYPE_HEALTH_PERCENT:
                buffer.writeFloatLE(this.healthPercent);
                break;
            case TYPE_TITLE:
                Binary.writeString(buffer, this.title);
                break;
        }
    }
}
