package cn.nukkit.raknet.server;

import cn.nukkit.raknet.RakNet;
import cn.nukkit.raknet.protocol.DataPacket;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.raknet.protocol.Packet;
import cn.nukkit.raknet.protocol.packet.*;
import cn.nukkit.utils.Binary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Session {
    public final static int STATE_UNCONNECTED = 0;
    public final static int STATE_CONNECTING_1 = 1;
    public final static int STATE_CONNECTING_2 = 2;
    public final static int STATE_CONNECTED = 3;

    public final static int MAX_SPLIT_SIZE = 128;
    public final static int MAX_SPLIT_COUNT = 4;

    public static int WINDOW_SIZE = 2048;

    private int messageIndex = 0;
    private Map<Integer, Integer> channelIndex = new ConcurrentHashMap<>();

    private SessionManager sessionManager;
    private String address;
    private int port;
    private int state = STATE_UNCONNECTED;
    //private List<EncapsulatedPacket> preJoinQueue = new ArrayList<>();
    private int mtuSize = 548; //Min size
    private long id = 0;
    private int splitID = 0;

    private int sendSeqNumber = 0;
    private int lastSeqNumber = -1;

    private long lastUpdate;
    private long startTime;

    private boolean isTemporal = true;

    private List<DataPacket> packetToSend = new ArrayList<>();

    private boolean isActive;

    private List<Integer> ACKQueue = new ArrayList<>();
    private List<Integer> NACKQueue = new ArrayList<>();

    private Map<Integer, DataPacket> recoveryQueue = new ConcurrentHashMap<>();

    private Map<Integer, Map<Integer, EncapsulatedPacket>> splitPackets = new ConcurrentHashMap<>();

    private Map<Integer, Map<Integer, Integer>> needACK = new ConcurrentHashMap<>();

    private DataPacket sendQueue;

    private int windowStart;
    private Map<Integer, Integer> receivedWindow = new ConcurrentHashMap<>();
    private int windowEnd;

    private int reliableWindowStart;
    private int reliableWindowEnd;
    private Map<Integer, EncapsulatedPacket> reliableWindow = new ConcurrentHashMap<>();
    private int lastReliableIndex = -1;

    public Session(SessionManager sessionManager, String address, int port) {
        this.sessionManager = sessionManager;
        this.address = address;
        this.port = port;
        this.sendQueue = new DATA_PACKET_4();
        this.lastUpdate = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.isActive = false;
        this.windowStart = -1;
        this.windowEnd = WINDOW_SIZE;

        this.reliableWindowStart = 0;
        this.reliableWindowEnd = WINDOW_SIZE;

        for (int i = 0; i < 32; i++) {
            this.channelIndex.put(i, 0);
        }
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public long getID() {
        return this.id;
    }

    public void update(long time) throws Exception {
        if (!this.isActive && (this.lastUpdate + 10000) < time) { //10 second timeout
            this.disconnect("timeout");

            return;
        }
        this.isActive = false;

        if (!this.ACKQueue.isEmpty()) {
            ACK pk = new ACK();
            pk.packets = this.ACKQueue.stream().toArray(Integer[]::new);
            this.sendPacket(pk);
            this.ACKQueue.clear();
        }

        if (!this.NACKQueue.isEmpty()) {
            NACK pk = new NACK();
            pk.packets = this.NACKQueue.stream().toArray(Integer[]::new);
            this.sendPacket(pk);
            this.NACKQueue.clear();
        }

        if (!this.packetToSend.isEmpty()) {
            int limit = 16;
            for (int i = 0; i < this.packetToSend.size(); i++) {
                DataPacket pk = this.packetToSend.get(i);
                pk.sendTime = time;
                pk.encode();
                this.recoveryQueue.put(pk.seqNumber, pk);
                this.packetToSend.remove(pk);
                this.sendPacket(pk);

                if (limit-- <= 0) {
                    break;
                }
            }
        }

        if (this.packetToSend.size() > WINDOW_SIZE) {
            this.packetToSend.clear();
        }

        if (!this.needACK.isEmpty()) {
            for (Map.Entry<Integer, Map<Integer, Integer>> entry : this.needACK.entrySet()) {
                int identifierACK = entry.getKey();
                Map<Integer, Integer> indexes = entry.getValue();
                if (indexes.isEmpty()) {
                    this.needACK.remove(identifierACK);
                    this.sessionManager.notifyACK(this, identifierACK);
                }
            }
        }

        for (Map.Entry<Integer, DataPacket> entry : this.recoveryQueue.entrySet()) {
            int seq = entry.getKey();
            DataPacket pk = entry.getValue();
            if (pk.sendTime < System.currentTimeMillis() - 8000) {
                this.packetToSend.add(pk);
                this.recoveryQueue.remove(seq);
            } else {
                break;
            }
        }

        for (Integer seq : this.receivedWindow.keySet()) {
            if (seq < this.windowStart) {
                this.receivedWindow.remove(seq);
            } else {
                break;
            }
        }

        this.sendQueue();
    }

    public void disconnect() throws Exception {
        this.disconnect("unknown");
    }

    public void disconnect(String reason) throws Exception {
        this.sessionManager.removeSession(this, reason);
    }

    private void sendPacket(Packet packet) throws IOException {
        this.sessionManager.sendPacket(packet, this.address, this.port);
    }

    public void sendQueue() throws IOException {
        if (!this.sendQueue.packets.isEmpty()) {
            this.sendQueue.seqNumber = sendSeqNumber++;
            this.sendPacket(sendQueue);
            this.sendQueue.sendTime = System.currentTimeMillis();
            this.recoveryQueue.put(this.sendQueue.seqNumber, this.sendQueue);
            this.sendQueue = new DATA_PACKET_4();
        }
    }

    private void addToQueue(EncapsulatedPacket pk) throws Exception {
        addToQueue(pk, RakNet.PRIORITY_NORMAL);
    }

    private void addToQueue(EncapsulatedPacket pk, int flags) throws Exception {
        int priority = flags & 0b0000111;
        if (pk.needACK && pk.messageIndex != null) {
            Map<Integer, Integer> map;
            if (this.needACK.get(pk.identifierACK) != null) {
                map = this.needACK.get(pk.identifierACK);
                map.put(pk.messageIndex, pk.messageIndex);
            } else {
                map = new ConcurrentHashMap<>();
                map.put(pk.messageIndex, pk.messageIndex);
            }
            this.needACK.put(pk.identifierACK, map);
        }

        if (priority == RakNet.PRIORITY_IMMEDIATE) { //Skip queues
            DataPacket packet = new DATA_PACKET_0();
            packet.seqNumber = this.sendSeqNumber++;
            if (pk.needACK) {
                packet.packets.add(pk.clone());
                pk.needACK = false;
            } else {
                packet.packets.add(pk.toBinary());
            }

            this.sendPacket(packet);
            packet.sendTime = System.currentTimeMillis();
            this.recoveryQueue.put(packet.seqNumber, packet);
            return;
        }
        int length = this.sendQueue.length();
        if (length + pk.getTotalLength() > this.mtuSize) {
            this.sendQueue();
        }

        if (pk.needACK) {
            this.sendQueue.packets.add(pk.clone());
            pk.needACK = false;
        } else {
            this.sendQueue.packets.add(pk.toBinary());
        }
    }

    public void addEncapsulatedToQueue(EncapsulatedPacket packet) throws Exception {
        addEncapsulatedToQueue(packet, RakNet.PRIORITY_NORMAL);
    }

    public void addEncapsulatedToQueue(EncapsulatedPacket packet, int flags) throws Exception {
        if ((packet.needACK = (flags & RakNet.FLAG_NEED_ACK) > 0)) {
            this.needACK.put(packet.identifierACK, new ConcurrentHashMap<>());
        }

        if (packet.reliability == 2 ||
                packet.reliability == 3 ||
                packet.reliability == 4 ||
                packet.reliability == 6 ||
                packet.reliability == 7
                ) {
            packet.messageIndex = this.messageIndex++;

            if (packet.reliability == 3) {
                this.channelIndex.put(packet.orderChannel, this.channelIndex.get(packet.orderChannel) + 1);
                packet.orderIndex = this.channelIndex.get(packet.orderChannel);
            }
        }

        if (packet.getTotalLength() + 4 > this.mtuSize) {
            byte[][] buffers = Binary.splitBytes(packet.buffer, this.mtuSize - 34);
            int splitID = ++this.splitID % 65536;
            for (int count = 0; count < buffers.length; count++) {
                byte[] buffer = buffers[count];
                EncapsulatedPacket pk = new EncapsulatedPacket();
                pk.splitID = splitID;
                pk.hasSplit = true;
                pk.splitCount = buffers.length;
                pk.reliability = packet.reliability;
                pk.splitIndex = count;
                pk.buffer = buffer;
                if (count > 0) {
                    pk.messageIndex = this.messageIndex++;
                } else {
                    pk.messageIndex = packet.messageIndex;
                }
                if (pk.reliability == 3) {
                    pk.orderChannel = packet.orderChannel;
                    pk.orderIndex = packet.orderIndex;
                }
                addToQueue(pk, flags | RakNet.PRIORITY_IMMEDIATE);
            }
        } else {
            addToQueue(packet, flags);
        }
    }

    private void handleSplit(EncapsulatedPacket packet) throws Exception {
        if (packet.splitCount >= MAX_SPLIT_SIZE || packet.splitIndex >= MAX_SPLIT_SIZE || packet.splitIndex < 0) {
            return;
        }

        if (!this.splitPackets.containsKey(packet.splitID)) {
            if (this.splitPackets.size() >= MAX_SPLIT_COUNT) {
                return;
            }
            Map<Integer, EncapsulatedPacket> map = new ConcurrentHashMap<>();
            map.put(packet.splitIndex, packet);
            this.splitPackets.put(packet.splitID, map);
        } else {
            Map<Integer, EncapsulatedPacket> map = this.splitPackets.get(packet.splitID);
            map.put(packet.splitIndex, packet);
            this.splitPackets.put(packet.splitID, map);
        }

        if (this.splitPackets.get(packet.splitID).values().size() == packet.splitCount) {
            EncapsulatedPacket pk = new EncapsulatedPacket();
            pk.buffer = new byte[0];
            for (int i = 0; i < packet.splitCount; i++) {
                pk.buffer = Binary.appendBytes(pk.buffer, this.splitPackets.get(packet.splitID).get(i).buffer);
            }
            pk.length = pk.buffer.length;
            this.splitPackets.remove(packet.splitID);

            this.handleEncapsulatedPacketRoute(pk);
        }
    }

    private void handleEncapsulatedPacket(EncapsulatedPacket packet) throws Exception {
        if (packet.messageIndex == null) {
            this.handleEncapsulatedPacketRoute(packet);
        } else {
            if (packet.messageIndex < this.reliableWindowStart || packet.messageIndex > this.reliableWindowEnd) {
                return;
            }

            if ((packet.messageIndex - this.lastReliableIndex) == 1) {
                this.lastReliableIndex++;
                this.reliableWindowStart++;
                this.reliableWindowEnd++;
                this.handleEncapsulatedPacketRoute(packet);

                if (!this.reliableWindow.isEmpty()) {

                    for (Map.Entry<Integer, EncapsulatedPacket> entry : this.reliableWindow.entrySet()) {
                        Integer index = entry.getKey();
                        EncapsulatedPacket pk = entry.getValue();

                        if ((index - this.lastReliableIndex) != 1) {
                            break;
                        }
                        this.lastReliableIndex++;
                        this.reliableWindowStart++;
                        this.reliableWindowEnd++;
                        this.handleEncapsulatedPacketRoute(pk);
                        this.reliableWindow.remove(index);
                    }
                }
            } else {
                this.reliableWindow.put(packet.messageIndex, packet);
            }
        }
    }

    public int getState() {
        return state;
    }

    public boolean isTemporal() {
        return isTemporal;
    }

    private void handleEncapsulatedPacketRoute(EncapsulatedPacket packet) throws Exception {
        if (this.sessionManager == null) {
            return;
        }

        if (packet.hasSplit) {
            if (this.state == STATE_CONNECTED) {
                this.handleSplit(packet);
            }
            return;
        }

        byte id = packet.buffer[0];
        if (id < (byte) 0x80) { //internal data packet
            if (state == STATE_CONNECTING_2) {
                if (id == CLIENT_CONNECT_DataPacket.ID) {
                    CLIENT_CONNECT_DataPacket dataPacket = new CLIENT_CONNECT_DataPacket();
                    dataPacket.buffer = packet.buffer;
                    dataPacket.decode();
                    SERVER_HANDSHAKE_DataPacket pk = new SERVER_HANDSHAKE_DataPacket();
                    pk.address = this.address;
                    pk.port = this.port;
                    pk.sendPing = dataPacket.sendPing;
                    pk.sendPong = dataPacket.sendPing + 1000L;
                    pk.encode();

                    EncapsulatedPacket sendPacket = new EncapsulatedPacket();
                    sendPacket.reliability = 0;
                    sendPacket.buffer = pk.buffer;
                    this.addToQueue(sendPacket, RakNet.PRIORITY_IMMEDIATE);
                } else if (id == CLIENT_HANDSHAKE_DataPacket.ID) {
                    CLIENT_HANDSHAKE_DataPacket dataPacket = new CLIENT_HANDSHAKE_DataPacket();
                    dataPacket.buffer = packet.buffer;
                    dataPacket.decode();

                    if (dataPacket.port == this.sessionManager.getPort() || !this.sessionManager.portChecking) {
                        this.state = STATE_CONNECTED; //FINALLY!
                        this.isTemporal = false;
                        this.sessionManager.openSession(this);
                        /*for (EncapsulatedPacket p : this.preJoinQueue) {
                            this.sessionManager.streamEncapsulated(this, p);
                        }
                        this.preJoinQueue.clear();*/
                    }
                }
            } else if (id == CLIENT_DISCONNECT_DataPacket.ID) {
                disconnect("client disconnect");
            } else if (id == PING_DataPacket.ID) {
                PING_DataPacket dataPacket = new PING_DataPacket();
                dataPacket.buffer = packet.buffer;
                dataPacket.decode();

                PONG_DataPacket pk = new PONG_DataPacket();
                pk.pingID = dataPacket.pingID;
                pk.encode();

                EncapsulatedPacket sendPacket = new EncapsulatedPacket();
                sendPacket.reliability = 0;
                sendPacket.buffer = pk.buffer;
                this.addToQueue(sendPacket);
                //TODO: add PING/PONG (0x00/0x03) automatic latency measure
            } else if (state == STATE_CONNECTED) {
                this.sessionManager.streamEncapsulated(this, packet);
                //TODO: stream channels
            } else {
                //this.sessionManager.getLogger().notice("Received packet before connection: "+Binary.bytesToHexString(packet.buffer));
            }
        }
    }

    public void handlePacket(Packet packet) throws Exception {
        this.isActive = true;
        this.lastUpdate = System.currentTimeMillis();
        if (this.state == STATE_CONNECTED || this.state == STATE_CONNECTING_2) {
            if ((packet.buffer[0] >= (byte) 0x80 || packet.buffer[0] <= (byte) 0x8f) && packet instanceof DataPacket) {
                packet.decode();

                DataPacket dp = (DataPacket) packet;
                if (dp.seqNumber < this.windowStart || dp.seqNumber > this.windowEnd || this.receivedWindow.containsKey(dp.seqNumber)) {
                    return;
                }

                int diff = dp.seqNumber - this.lastSeqNumber;

                this.NACKQueue.remove(dp.seqNumber);
                this.ACKQueue.add(dp.seqNumber);
                this.receivedWindow.put(dp.seqNumber, dp.seqNumber);

                if (diff != 1) {
                    for (int i = this.lastSeqNumber + 1; i < dp.seqNumber; i++) {
                        if (!this.receivedWindow.containsKey(i)) {
                            this.NACKQueue.add(i);
                        }
                    }
                }

                if (diff >= 1) {
                    this.lastSeqNumber = dp.seqNumber;
                    this.windowStart += diff;
                    this.windowEnd += diff;
                }

                for (Object pk : dp.packets) {
                    if (pk instanceof EncapsulatedPacket) {
                        this.handleEncapsulatedPacket((EncapsulatedPacket) pk);
                    }
                }
            } else {
                if (packet instanceof ACK) {
                    packet.decode();
                    for (int seq : ((ACK) packet).packets) {
                        if (this.recoveryQueue.containsKey(seq)) {
                            for (Object pk : this.recoveryQueue.get(seq).packets) {
                                if (pk instanceof EncapsulatedPacket && ((EncapsulatedPacket) pk).needACK && ((EncapsulatedPacket) pk).messageIndex != null) {
                                    if (this.needACK.containsKey(((EncapsulatedPacket) pk).identifierACK)) {
                                        Map<Integer, Integer> map = this.needACK.get(((EncapsulatedPacket) pk).identifierACK);
                                        map.remove(((EncapsulatedPacket) pk).messageIndex);
                                        this.needACK.put(((EncapsulatedPacket) pk).identifierACK, map);
                                    }
                                }
                                this.recoveryQueue.remove(seq);
                            }
                        }
                    }
                } else if (packet instanceof NACK) {
                    packet.decode();
                    for (Integer seq : ((NACK) packet).packets) {
                        if (this.recoveryQueue.containsKey(seq)) {
                            DataPacket pk = this.recoveryQueue.get(seq);
                            pk.seqNumber = this.sendSeqNumber++;
                            this.packetToSend.add(pk);
                            this.recoveryQueue.remove(seq);
                        }
                    }
                }
            }
        } else if (packet.buffer[0] > (byte) 0x00 || packet.buffer[0] < (byte) 0x80) { //Not Data packet :)
            packet.decode();
            /*if (packet instanceof UNCONNECTED_PING) {
                UNCONNECTED_PONG pk = new UNCONNECTED_PONG();
                pk.serverID = this.sessionManager.getID();
                pk.pingID = ((UNCONNECTED_PING) packet).pingID;
                pk.serverName = this.sessionManager.getName();
                this.sendPacket(pk);
            } else*/
            if (packet instanceof OPEN_CONNECTION_REQUEST_1) {
                //TODO: check protocol number and refuse connections
                OPEN_CONNECTION_REPLY_1 pk = new OPEN_CONNECTION_REPLY_1();
                pk.mtuSize = ((OPEN_CONNECTION_REQUEST_1) packet).mtuSize;
                pk.serverID = sessionManager.getID();
                this.sendPacket(pk);
                this.state = STATE_CONNECTING_1;
            } else if (this.state == STATE_CONNECTING_1 && packet instanceof OPEN_CONNECTION_REQUEST_2) {
                this.id = ((OPEN_CONNECTION_REQUEST_2) packet).clientID;
                if (((OPEN_CONNECTION_REQUEST_2) packet).serverPort == this.sessionManager.getPort() || !this.sessionManager.portChecking) {
                    this.mtuSize = Math.min(Math.abs(((OPEN_CONNECTION_REQUEST_2) packet).mtuSize), 1464); //Max size, do not allow creating large buffers to fill server memory
                    OPEN_CONNECTION_REPLY_2 pk = new OPEN_CONNECTION_REPLY_2();
                    pk.mtuSize = (short) this.mtuSize;
                    pk.serverID = this.sessionManager.getID();
                    pk.clientAddress = this.address;
                    pk.clientPort = this.port;
                    this.sendPacket(pk);
                    this.state = STATE_CONNECTING_2;
                }
            }
        }
    }

    public void close() throws Exception {
        byte[] data = new byte[]{0x00, 0x00, 0x08, 0x15}; //CLIENT_DISCONNECT packet 0x15
        this.addEncapsulatedToQueue(EncapsulatedPacket.fromBinary(data), RakNet.PRIORITY_IMMEDIATE);
        this.sessionManager = null;
    }
}
