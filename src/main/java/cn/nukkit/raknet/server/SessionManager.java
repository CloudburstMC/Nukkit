package cn.nukkit.raknet.server;

import cn.nukkit.raknet.RakNet;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.raknet.protocol.Packet;
import cn.nukkit.raknet.protocol.packet.*;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ThreadedLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SessionManager {
    protected Map<Byte, Packet> packetPool = new ConcurrentHashMap<>();

    protected RakNetServer server;

    protected UDPServerSocket socket;

    protected int receiveBytes = 0;
    protected int sendBytes = 0;

    protected Map<String, Session> sessions = new ConcurrentHashMap<>();

    protected String name = "";

    protected int packetLimit = 1000;

    protected boolean shutdown = false;

    protected long ticks = 0;
    protected long lastMeasure;

    protected Map<String, Long> block = new ConcurrentHashMap<>();
    protected Map<String, Integer> ipSec = new ConcurrentHashMap<>();

    public boolean portChecking = false;

    public long serverId;

    public SessionManager(RakNetServer server, UDPServerSocket socket) throws Exception {
        this.server = server;
        this.socket = socket;
        this.registerPackets();

        this.serverId = new Random().nextLong();

        this.run();
    }

    public int getPort() {
        return this.server.port;
    }

    public ThreadedLogger getLogger() {
        return this.server.getLogger();
    }

    public void run() throws Exception {
        this.tickProcessor();
    }

    private void tickProcessor() throws Exception {
        this.lastMeasure = System.currentTimeMillis();
        while (!this.shutdown) {
            long start = System.currentTimeMillis();
            int max = 5000;
            while (max > 0 && this.receivePacket()) {
                --max;
            }
            while (this.receiveStream()) ;

            long time = System.currentTimeMillis() - start;
            if (time < 50) {
                try {
                    Thread.sleep(System.currentTimeMillis() + 50 - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.tick();
            }
        }
    }

    private void tick() throws Exception {
        long time = System.currentTimeMillis();
        for (Session session : this.sessions.values()) {
            session.update(time);
        }

        for (Map.Entry<String, Integer> entry : this.ipSec.entrySet()) {
            String address = entry.getKey();
            int count = entry.getValue();
            if (count >= this.packetLimit) {
                this.blockAddress(address);
            }
        }
        this.ipSec.clear();

        if ((this.ticks & 0b1111) == 0) {
            double diff = Math.max(50d, (double) time - this.lastMeasure);
            this.streamOption("bandwidth", this.sendBytes / diff + ";" + this.receiveBytes / diff);
            this.lastMeasure = time;
            this.sendBytes = 0;
            this.receiveBytes = 0;

            if (!this.block.isEmpty()) {
                long now = System.currentTimeMillis();
                for (Map.Entry<String, Long> entry : this.block.entrySet()) {
                    String address = entry.getKey();
                    long timeout = entry.getValue();
                    if (timeout <= now) {
                        this.block.remove(address);
                    } else {
                        break;
                    }
                }
            }
        }

        ++this.ticks;
    }

    private boolean receivePacket() throws Exception {
        DatagramPacket datagramPacket = this.socket.readPacket();
        if (datagramPacket != null) {
            int len = datagramPacket.getLength();
            byte[] buffer = datagramPacket.getData();
            String source = datagramPacket.getAddress().getHostAddress();
            int port = datagramPacket.getPort();
            if (len > 0) {
                this.receiveBytes += len;
                if (this.block.containsKey(source)) {
                    return true;
                }

                if (this.ipSec.containsKey(source)) {
                    this.ipSec.put(source, this.ipSec.get(source) + 1);
                } else {
                    this.ipSec.put(source, 1);
                }

                Packet packet = this.getPacketFromPool(buffer[0]);
                if (packet != null) {
                    packet.buffer = buffer;
                    this.getSession(source, port).handlePacket(packet);
                    return true;
                } else if (buffer.length == 0) {
                    this.streamRAW(source, port, buffer);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public void sendPacket(Packet packet, String dest, int port) throws IOException {
        packet.encode();
        this.sendBytes += this.socket.writePacket(packet.buffer, dest, port);
    }

    public void sendPacket(Packet packet, InetSocketAddress dest) throws IOException {
        packet.encode();
        this.sendBytes += this.socket.writePacket(packet.buffer, dest);
    }

    public void streamEncapsulated(Session session, EncapsulatedPacket packet) {
        this.streamEncapsulated(session, packet, RakNet.PRIORITY_NORMAL);
    }

    public void streamEncapsulated(Session session, EncapsulatedPacket packet, int flags) {
        String id = session.getAddress() + ":" + session.getPort();
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_ENCAPSULATED,
                new byte[]{(byte) (id.length() & 0xff)},
                id.getBytes(StandardCharsets.UTF_8),
                new byte[]{(byte) (flags & 0xff)},
                packet.toBinary(true)
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    public void streamRAW(String address, int port, byte[] payload) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_RAW,
                new byte[]{(byte) (address.length() & 0xff)},
                address.getBytes(StandardCharsets.UTF_8),
                Binary.writeShort((short) port),
                payload
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    protected void streamClose(String identifier, String reason) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_CLOSE_SESSION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8),
                new byte[]{(byte) (reason.length() & 0xff)},
                reason.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    protected void streamInvalid(String identifier) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_INVALID_SESSION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    protected void streamOpen(Session session) {
        String identifier = session.getAddress() + ":" + session.getPort();
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_OPEN_SESSION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8),
                new byte[]{(byte) (session.getAddress().length() & 0xff)},
                session.getAddress().getBytes(StandardCharsets.UTF_8),
                Binary.writeShort((short) session.getPort()),
                Binary.writeLong(session.getID())
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    protected void streamACK(String identifier, int identifierACK) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_ACK_NOTIFICATION,
                new byte[]{(byte) (identifier.length() & 0xff)},
                identifier.getBytes(StandardCharsets.UTF_8),
                Binary.writeInt(identifierACK)
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    protected void streamOption(String name, String value) {
        byte[] buffer = Binary.appendBytes(
                RakNet.PACKET_SET_OPTION,
                new byte[]{(byte) (name.length() & 0xff)},
                name.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8)
        );
        this.server.pushThreadToMainPacket(buffer);
    }

    public boolean receiveStream() throws Exception {
        byte[] packet = this.server.readMainToThreadPacket();
        if (packet != null && packet.length > 0) {
            byte id = packet[0];
            int offset = 1;
            switch (id) {
                case RakNet.PACKET_ENCAPSULATED:
                    int len = packet[offset++];
                    String identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    offset += len;
                    if (this.sessions.containsKey(identifier)) {
                        byte flags = packet[offset++];
                        byte[] buffer = Binary.subBytes(packet, offset);
                        this.sessions.get(identifier).addEncapsulatedToQueue(EncapsulatedPacket.fromBinary(buffer, true), flags);
                    } else {
                        this.streamInvalid(identifier);
                    }
                    break;
                case RakNet.PACKET_RAW:
                    len = packet[offset++];
                    String address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    offset += len;
                    short port = Binary.readShort(Binary.subBytes(packet, offset, 2));
                    offset += 2;
                    byte[] payload = Binary.subBytes(packet, offset);
                    this.socket.writePacket(payload, address, port);
                    break;
                case RakNet.PACKET_CLOSE_SESSION:
                    len = packet[offset++];
                    identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    if (this.sessions.containsKey(identifier)) {
                        this.removeSession(this.sessions.get(identifier));
                    } else {
                        this.streamInvalid(identifier);
                    }
                    break;
                case RakNet.PACKET_INVALID_SESSION:
                    len = packet[offset++];
                    identifier = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    if (this.sessions.containsKey(identifier)) {
                        this.removeSession(this.sessions.get(identifier));
                    }
                    break;
                case RakNet.PACKET_SET_OPTION:
                    len = packet[offset++];
                    String name = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    offset += len;
                    String value = new String(Binary.subBytes(packet, offset), StandardCharsets.UTF_8);
                    switch (name) {
                        case "name":
                            this.name = value;
                            break;
                        case "portChecking":
                            this.portChecking = Boolean.valueOf(value);
                            break;
                        case "packetLimit":
                            this.packetLimit = Integer.valueOf(value);
                            break;
                    }
                    break;
                case RakNet.PACKET_BLOCK_ADDRESS:
                    len = packet[offset++];
                    address = new String(Binary.subBytes(packet, offset, len), StandardCharsets.UTF_8);
                    offset += len;
                    int timeout = Binary.readInt(Binary.subBytes(packet, offset, 4));
                    this.blockAddress(address, timeout);
                    break;
                case RakNet.PACKET_SHUTDOWN:
                    for (Session session : this.sessions.values()) {
                        this.removeSession(session);
                    }

                    this.socket.close();
                    this.shutdown = true;
                    break;
                case RakNet.PACKET_EMERGENCY_SHUTDOWN:
                    this.shutdown = true;
                default:
                    return false;
            }
            return true;
        }

        return false;
    }

    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    public void blockAddress(String address, int timeout) {
        long finalTime = System.currentTimeMillis() + 300 * 1000;
        if (!this.block.containsKey(address) || timeout == -1) {
            if (timeout == -1) {
                finalTime = Long.MAX_VALUE;
            } else {
                this.getLogger().notice("[RakNet Thread #" + Thread.currentThread().getId() + "] Blocked " + address + " for " + timeout + " seconds");
            }
            this.block.put(address, finalTime);
        } else if (this.block.get(address) < finalTime) {
            this.block.put(address, finalTime);
        }
    }

    public Session getSession(String ip, int port) {
        String id = ip + ":" + port;
        if (!this.sessions.containsKey(id)) {
            Session session = new Session(this, ip, port);
            this.sessions.put(id, session);
            return session;
        }

        return this.sessions.get(id);
    }

    public void removeSession(Session session) throws Exception {
        this.removeSession(session, "unknown");
    }

    public void removeSession(Session session, String reason) throws Exception {
        String id = session.getAddress() + ":" + session.getPort();
        if (this.sessions.containsKey(id)) {
            this.sessions.get(id).close();
            this.sessions.remove(id);
            this.streamClose(id, reason);
        }
    }

    public void openSession(Session session) {
        this.streamOpen(session);
    }

    public void notifyACK(Session session, int identifierACK) {
        this.streamACK(session.getAddress() + ":" + session.getPort(), identifierACK);
    }

    public String getName() {
        return name;
    }

    public long getID() {
        return this.serverId;
    }

    private void registerPacket(byte id, Class<? extends Packet> clazz) {
        try {
            this.packetPool.put(id, clazz.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Packet getPacketFromPool(byte id) {
        if (this.packetPool.containsKey(id)) {
            try {
                return this.packetPool.get(id).clone();
            } catch (CloneNotSupportedException e) {
                //ignore
            }
        }
        return null;
    }

    private void registerPackets() {
        this.registerPacket(UNCONNECTED_PING.ID, UNCONNECTED_PING.class);
        this.registerPacket(UNCONNECTED_PING_OPEN_CONNECTIONS.ID, UNCONNECTED_PING_OPEN_CONNECTIONS.class);
        this.registerPacket(OPEN_CONNECTION_REQUEST_1.ID, OPEN_CONNECTION_REQUEST_1.class);
        this.registerPacket(OPEN_CONNECTION_REPLY_1.ID, OPEN_CONNECTION_REPLY_1.class);
        this.registerPacket(OPEN_CONNECTION_REQUEST_2.ID, OPEN_CONNECTION_REQUEST_2.class);
        this.registerPacket(OPEN_CONNECTION_REPLY_2.ID, OPEN_CONNECTION_REPLY_2.class);
        this.registerPacket(UNCONNECTED_PONG.ID, UNCONNECTED_PONG.class);
        this.registerPacket(ADVERTISE_SYSTEM.ID, ADVERTISE_SYSTEM.class);
        this.registerPacket(DATA_PACKET_0.ID, DATA_PACKET_0.class);
        this.registerPacket(DATA_PACKET_1.ID, DATA_PACKET_1.class);
        this.registerPacket(DATA_PACKET_2.ID, DATA_PACKET_2.class);
        this.registerPacket(DATA_PACKET_3.ID, DATA_PACKET_3.class);
        this.registerPacket(DATA_PACKET_4.ID, DATA_PACKET_4.class);
        this.registerPacket(DATA_PACKET_5.ID, DATA_PACKET_5.class);
        this.registerPacket(DATA_PACKET_6.ID, DATA_PACKET_6.class);
        this.registerPacket(DATA_PACKET_7.ID, DATA_PACKET_7.class);
        this.registerPacket(DATA_PACKET_8.ID, DATA_PACKET_8.class);
        this.registerPacket(DATA_PACKET_9.ID, DATA_PACKET_9.class);
        this.registerPacket(DATA_PACKET_A.ID, DATA_PACKET_A.class);
        this.registerPacket(DATA_PACKET_B.ID, DATA_PACKET_B.class);
        this.registerPacket(DATA_PACKET_C.ID, DATA_PACKET_C.class);
        this.registerPacket(DATA_PACKET_D.ID, DATA_PACKET_D.class);
        this.registerPacket(DATA_PACKET_E.ID, DATA_PACKET_E.class);
        this.registerPacket(DATA_PACKET_F.ID, DATA_PACKET_F.class);
        this.registerPacket(NACK.ID, NACK.class);
        this.registerPacket(ACK.ID, ACK.class);
    }
}
