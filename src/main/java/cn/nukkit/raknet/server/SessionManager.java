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
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SessionManager {
    protected final Packet.PacketFactory[] packetPool = new Packet.PacketFactory[256];

    protected RakNetServer server;

    protected UDPServerSocket socket;

    protected int receiveBytes = 0;
    protected int sendBytes = 0;

    protected Map<String, Session> sessions = new HashMap<>();

    protected String name = "";

    protected int packetLimit = 1000;

    protected boolean shutdown = false;

    protected long ticks = 0;
    protected long lastMeasure;

    protected Map<String, Long> block = new HashMap<>();
    protected Map<String, Integer> ipSec = new HashMap<>();

    public boolean portChecking = true;

    public long serverId;

    protected String currentSource = "";

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
            while (max > 0) {
                try {
                    if (!this.receivePacket()) {
                        break;
                    }
                    --max;
                } catch (Exception e) {
                    if (currentSource != "") {
                        this.blockAddress(currentSource);
                    }
                    // else ignore
                }
            }
            while (this.receiveStream()) ;

            long time = System.currentTimeMillis() - start;
            if (time < 50) {
                try {
                    Thread.sleep(50 - time);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
            this.tick();
        }
    }

    private void tick() throws Exception {
        long time = System.currentTimeMillis();
        for (Session session : new ArrayList<>(this.sessions.values())) {
            session.update(time);
        }

        for (String address : this.ipSec.keySet()) {
            int count = this.ipSec.get(address);
            if (count >= this.packetLimit) {
                this.blockAddress(address);
            }
        }
        this.ipSec.clear();

        if ((this.ticks & 0b1111) == 0) {
            double diff = Math.max(5d, (double) time - this.lastMeasure);
            this.streamOption("bandwidth", this.sendBytes / diff + ";" + this.receiveBytes / diff);
            this.lastMeasure = time;
            this.sendBytes = 0;
            this.receiveBytes = 0;

            if (!this.block.isEmpty()) {
                long now = System.currentTimeMillis();
                for (String address : new ArrayList<>(this.block.keySet())) {
                    long timeout = this.block.get(address);
                    if (timeout <= now) {
                        this.block.remove(address);
                        this.getLogger().notice("Unblocked " + address);
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
            currentSource = source; //in order to block address
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

                byte pid = buffer[0];

                if (pid == UNCONNECTED_PONG.ID) {
                    return false;
                }

                Packet packet = this.getPacketFromPool(pid);
                if (packet != null) {
                    packet.buffer = buffer;
                    this.getSession(source, port).handlePacket(packet);
                    return true;
                } else if (pid == UNCONNECTED_PING.ID) {
                    packet = new UNCONNECTED_PING();
                    packet.buffer = buffer;
                    packet.decode();

                    UNCONNECTED_PONG pk = new UNCONNECTED_PONG();
                    pk.serverID = this.getID();
                    pk.pingID = ((UNCONNECTED_PING) packet).pingID;
                    pk.serverName = this.getName();
                    this.sendPacket(pk, source, port);
                } else if (buffer.length != 0) {
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
                Binary.writeShort(port),
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
                Binary.writeShort(session.getPort()),
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

    private void checkSessions() {
        int size = this.sessions.size();
        if (size > 4096) {
            List<String> keyToRemove = new ArrayList<>();
            for (String i : this.sessions.keySet()) {
                Session s = this.sessions.get(i);
                if (s.isTemporal()) {
                    keyToRemove.add(i);
                    size--;
                    if (size <= 4096) {
                        break;
                    }
                }
            }

            for (String i : keyToRemove) {
                this.sessions.remove(i);
            }
        }
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
                    int port = Binary.readShort(Binary.subBytes(packet, offset, 2));
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
                    for (Session session : new ArrayList<>(this.sessions.values())) {
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
        long finalTime = System.currentTimeMillis() + timeout * 1000;
        if (!this.block.containsKey(address) || timeout == -1) {
            if (timeout == -1) {
                finalTime = Long.MAX_VALUE;
            } else {
                this.getLogger().notice("Blocked " + address + " for " + timeout + " seconds");
            }
            this.block.put(address, finalTime);
        } else if (this.block.get(address) < finalTime) {
            this.block.put(address, finalTime);
        }
    }

    public Session getSession(String ip, int port) {
        String id = ip + ":" + port;
        if (!this.sessions.containsKey(id)) {
            this.checkSessions();
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

    private void registerPacket(byte id, Packet.PacketFactory factory) {
        this.packetPool[id & 0xFF] = factory;
    }

    public Packet getPacketFromPool(byte id) {
        return this.packetPool[id & 0xFF].create();
    }

    private void registerPackets() {
        // fill with dummy returning null
        Arrays.fill(this.packetPool, new Packet.PacketFactory() {

            @Override
            public Packet create() {
                return null;
            }
        });

        //this.registerPacket(UNCONNECTED_PING.ID, UNCONNECTED_PING.class);
        this.registerPacket(UNCONNECTED_PING_OPEN_CONNECTIONS.ID, new UNCONNECTED_PING_OPEN_CONNECTIONS.Factory());
        this.registerPacket(OPEN_CONNECTION_REQUEST_1.ID, new OPEN_CONNECTION_REQUEST_1.Factory());
        this.registerPacket(OPEN_CONNECTION_REPLY_1.ID, new OPEN_CONNECTION_REPLY_1.Factory());
        this.registerPacket(OPEN_CONNECTION_REQUEST_2.ID, new OPEN_CONNECTION_REQUEST_2.Factory());
        this.registerPacket(OPEN_CONNECTION_REPLY_2.ID, new OPEN_CONNECTION_REPLY_2.Factory());
        this.registerPacket(UNCONNECTED_PONG.ID, new UNCONNECTED_PONG.Factory());
        this.registerPacket(ADVERTISE_SYSTEM.ID, new ADVERTISE_SYSTEM.Factory());
        this.registerPacket(DATA_PACKET_0.ID, new DATA_PACKET_0.Factory());
        this.registerPacket(DATA_PACKET_1.ID, new DATA_PACKET_1.Factory());
        this.registerPacket(DATA_PACKET_2.ID, new DATA_PACKET_2.Factory());
        this.registerPacket(DATA_PACKET_3.ID, new DATA_PACKET_3.Factory());
        this.registerPacket(DATA_PACKET_4.ID, new DATA_PACKET_4.Factory());
        this.registerPacket(DATA_PACKET_5.ID, new DATA_PACKET_5.Factory());
        this.registerPacket(DATA_PACKET_6.ID, new DATA_PACKET_6.Factory());
        this.registerPacket(DATA_PACKET_7.ID, new DATA_PACKET_7.Factory());
        this.registerPacket(DATA_PACKET_8.ID, new DATA_PACKET_8.Factory());
        this.registerPacket(DATA_PACKET_9.ID, new DATA_PACKET_9.Factory());
        this.registerPacket(DATA_PACKET_A.ID, new DATA_PACKET_A.Factory());
        this.registerPacket(DATA_PACKET_B.ID, new DATA_PACKET_B.Factory());
        this.registerPacket(DATA_PACKET_C.ID, new DATA_PACKET_C.Factory());
        this.registerPacket(DATA_PACKET_D.ID, new DATA_PACKET_D.Factory());
        this.registerPacket(DATA_PACKET_E.ID, new DATA_PACKET_E.Factory());
        this.registerPacket(DATA_PACKET_F.ID, new DATA_PACKET_F.Factory());
        this.registerPacket(NACK.ID, new NACK.Factory());
        this.registerPacket(ACK.ID, new ACK.Factory());
    }
}
