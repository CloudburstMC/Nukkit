package cn.nukkit.server.raknet.server;

import cn.nukkit.server.raknet.RakNet;
import cn.nukkit.server.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.server.utils.Binary;

import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ServerHandler {

    protected final RakNetServer server;

    protected final ServerInstance instance;

    public ServerHandler(RakNetServer server, ServerInstance instance) {
        this.server = server;
        this.instance = instance;
    }

    public void sendEncapsulated(String identifier, EncapsulatedPacket packet) {
        this.sendEncapsulated(identifier, packet, RakNet.PRIORITY_NORMAL);
    }

    public void sendEncapsulated(String identifier, EncapsulatedPacket packet, int flags) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_ENCAPSULATED,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8),
                new byte[]{(byte) (flags & 0xff)},
                packet.toBinary(true)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void sendRaw(String address, int port, byte[] payload) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_RAW,
                new byte[]{(byte) (address.length() & 0xff)},
                address.getBytes(StandardCharsets.UTF_8),
                Binary.writeShort(port),
                payload
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void closeSession(String identifier, String reason) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_CLOSE_SESSION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8),
                new byte[]{(byte) (reason.length() & 0xff)},
                reason.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void sendOption(String name, String value) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_SET_OPTION,
                new byte[]{(byte) (name.length() & 0xff)},
                name.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void blockAddress(String address, int timeout) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_BLOCK_ADDRESS,
                new byte[]{(byte) (address.length() & 0xff)},
                address.getBytes(StandardCharsets.UTF_8),
                Binary.writeInt(timeout)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void unblockAddress(String address) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_UNBLOCK_ADDRESS,
                new byte[]{(byte) (address.length() & 0xff)},
                address.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public void shutdown() {
        this.server.pushMainToThreadPacket(new byte[]{RakNet.PACKET_SHUTDOWN});
        this.server.shutdown();
        synchronized (this) {
            try {
                this.wait(20);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        try {
            this.server.join();
        } catch (InterruptedException e) {
            //ignore
        }
    }

    public void emergencyShutdown() {
        this.server.shutdown();
        this.server.pushMainToThreadPacket(new byte[]{RakNet.PACKET_EMERGENCY_SHUTDOWN});
    }

    protected void invalidSession(String identifier) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_INVALID_SESSION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushMainToThreadPacket(buffer);
    }

    public boolean handlePacket() {
        byte[] packet = this.server.readThreadToMainPacket();
        if (packet != null && packet.length > 0) {
            byte id = packet[0];
            int offset = 1;
            if (id == RakNet.PACKET_ENCAPSULATED) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int flags = packet[offset++];
                byte[] buffer = Binary.subBytes(packet, offset);
                this.instance.handleEncapsulated(identifier, EncapsulatedPacket.fromBinary(buffer, true), flags);
            } else if (id == RakNet.PACKET_RAW) {
                int len = packet[offset++];
                String address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int port = Binary.readShort(Binary.subBytes(packet, offset, 2)) & 0xffff;
                offset += 2;
                byte[] payload = Binary.subBytes(packet, offset);
                this.instance.handleRaw(address, port, payload);
            } else if (id == RakNet.PACKET_SET_OPTION) {
                int len = packet[offset++];
                String name = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                String value = new String(Binary.subBytes(packet, offset), StandardCharsets.UTF_8);
                this.instance.handleOption(name, value);
            } else if (id == RakNet.PACKET_OPEN_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                len = packet[offset++];
                String address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int port = Binary.readShort(Binary.subBytes(packet, offset, 2)) & 0xffff;
                offset += 2;
                long clientID = Binary.readLong(Binary.subBytes(packet, offset, 8));
                this.instance.openSession(identifier, address, port, clientID);
            } else if (id == RakNet.PACKET_CLOSE_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                len = packet[offset++];
                String reason = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                this.instance.closeSession(identifier, reason);
            } else if (id == RakNet.PACKET_INVALID_SESSION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                this.instance.closeSession(identifier, "Invalid session");
            } else if (id == RakNet.PACKET_ACK_NOTIFICATION) {
                int len = packet[offset++];
                String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                offset += len;
                int identifierACK = Binary.readInt(Binary.subBytes(packet, offset, 4));
                this.instance.notifyACK(identifier, identifierACK);
            }
            return true;
        }

        return false;
    }

}
