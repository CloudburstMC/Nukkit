package cn.nukkit.raknet;

/**
 * author: MagicDroidX
 * Nukkit Project
 * UDP network library that follows the RakNet protocol for Nukkit Project
 * This is not affiliated with Jenkins Software LLC nor RakNet.
 */
public abstract class RakNet {

    public static final String VERSION = "1.0.0";
    public static final byte PROTOCOL = 6;
    public static final byte[] MAGIC = new byte[]{
            (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0x00,
            (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe,
            (byte) 0xfd, (byte) 0xfd, (byte) 0xfd, (byte) 0xfd,
            (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
    };

    public static final byte PRIORITY_NORMAL = 0;
    public static final byte PRIORITY_IMMEDIATE = 1;

    public static final byte FLAG_NEED_ACK = 0b00001000;

    /*
     * ENCAPSULATED payload:
     * byte (identifier length)
     * byte[] (identifier)
     * byte (flags, last 3 bits, priority)
     * payload (binary internal EncapsulatedPacket)
     */
    public static final byte PACKET_ENCAPSULATED = 0x01;

    /*
     * OPEN_SESSION payload:
     * byte (identifier length)
     * byte[] (identifier)
     * byte (address length)
     * byte[] (address)
     * short (port)
     * long (clientID)
     */
    public static final byte PACKET_OPEN_SESSION = 0x02;

    /*
     * CLOSE_SESSION payload:
     * byte (identifier length)
     * byte[] (identifier)
     * string (reason)
     */
    public static final byte PACKET_CLOSE_SESSION = 0x03;

    /*
     * INVALID_SESSION payload:
     * byte (identifier length)
     * byte[] (identifier)
     */
    public static final byte PACKET_INVALID_SESSION = 0x04;

    /* SEND_QUEUE payload:
     * byte (identifier length)
     * byte[] (identifier)
     */
    public static final byte PACKET_SEND_QUEUE = 0x05;

    /*
     * ACK_NOTIFICATION payload:
     * byte (identifier length)
     * byte[] (identifier)
     * int (identifierACK)
     */
    public static final byte PACKET_ACK_NOTIFICATION = 0x06;

    /*
     * SET_OPTION payload:
     * byte (option name length)
     * byte[] (option name)
     * byte[] (option value)
     */
    public static final byte PACKET_SET_OPTION = 0x07;

    /*
     * RAW payload:
     * byte (address length)
     * byte[] (address from/to)
     * short (port)
     * byte[] (payload)
     */
    public static final byte PACKET_RAW = 0x08;

    /*
     * RAW payload:
     * byte (address length)
     * byte[] (address)
     * int (timeout)
     */
    public static final byte PACKET_BLOCK_ADDRESS = 0x09;

    /*
     * No payload
     *
     * Sends the disconnect message, removes sessions correctly, closes sockets.
     */
    public static final byte PACKET_SHUTDOWN = 0x7e;

    /*
     * No payload
     *
     * Leaves everything as-is and halts, other Threads can be in a post-crash condition.
     */
    public static final byte PACKET_EMERGENCY_SHUTDOWN = 0x7f;

}
