package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PlayStatusPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAY_STATUS_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    /**
     * Sent to confirm login success and move onto resource pack sequence
     */
    public static final int LOGIN_SUCCESS = 0;
    /**
     * Displays outdated client disconnection screen
     */
    public static final int LOGIN_FAILED_CLIENT = 1;
    /**
     * Displays outdated server disconnection screen
     */
    public static final int LOGIN_FAILED_SERVER = 2;
    /**
     * Spawns player into the world
     */
    public static final int PLAYER_SPAWN = 3;
    /**
     * Unknown
     */
    public static final int LOGIN_FAILED_INVALID_TENANT = 4;
    /**
     * Sent when an Education Edition client joins a Bedrock Edition server
     */
    public static final int LOGIN_FAILED_VANILLA_EDU = 5;
    /**
     * Sent when a Bedrock Edition client joins an EducationEdition server
     */
    public static final int LOGIN_FAILED_EDU_VANILLA = 6;
    /**
     * Sent to a split screen player when the server is full
     */
    public static final int LOGIN_FAILED_SERVER_FULL = 7;
    /**
     * Unknown
     */
    public static final int LOGIN_FAILED_EDITOR_TO_VANILLA_MISMATCH = 8;
    /**
     * Unknown
     */
    public static final int LOGIN_FAILED_VANILLA_TO_EDITOR_MISMATCH = 9;

    public int status;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.status);
    }
}
