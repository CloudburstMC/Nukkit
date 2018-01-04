package cn.nukkit.server.network.protocol;

/**
 * Created by CreeperFace on 30. 10. 2016.
 */
public class BossEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

    public long bossEid;
    public UpdateType type;
    public long playerEid;
    public float healthPercent;
    public String title = "";
    public short unknown;
    public int color;
    public int overlay;
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.bossEid = this.getEntityUniqueId();
        this.type = UpdateType.values()[(int) this.getUnsignedVarInt()];

        switch (this.type) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                this.playerEid = this.getEntityUniqueId();
                break;
            case SHOW:
                this.title = this.getString();
                this.healthPercent = this.getLFloat();
            case UNKNOWN:
                this.unknown = (short) this.getShort();
            case TEXTURE:
                this.color = (int) this.getUnsignedVarInt();
                this.overlay = (int) this.getUnsignedVarInt();
                break;
            case HEALTH_PERCENT:
                this.healthPercent = this.getLFloat();
                break;
            case TITLE:
                this.title = this.getString();
                break;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.bossEid);
        this.putUnsignedVarInt(this.type.ordinal());
        switch (this.type) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                this.putEntityUniqueId(this.playerEid);
                break;
            case SHOW:
                this.putString(this.title);
                this.putLFloat(this.healthPercent);
            case UNKNOWN:
                this.putShort(this.unknown);
            case TEXTURE:
                this.putUnsignedVarInt(this.color);
                this.putUnsignedVarInt(this.overlay);
                break;
            case HEALTH_PERCENT:
                this.putLFloat(this.healthPercent);
                break;
            case TITLE:
                this.putString(this.title);
                break;
        }
    }

    public enum UpdateType {
        /**
         * Shows the bossbar to the player.
         */
        SHOW,
        /**
         * Registers a player to a boss fight.
         */
        REGISTER_PLAYER,
        /**
         * Removes the bossbar from the client.
         */
        HIDE,
        /**
         * Unregisters a player from a boss fight.
         */
        UNREGISTER_PLAYER,
        /**
         * Appears not to be implemented. Currently bar percentage only appears to change in response to the target entity's health.
         */
        HEALTH_PERCENT,
        /**
         * Also appears to not be implemented. Title clientside sticks as the target entity's nametag, or their entity type name if not set.
         */
        TITLE,
        /**
         * Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this
         */
        UNKNOWN,
        /**
         * Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever.
         */
        TEXTURE
    }
}