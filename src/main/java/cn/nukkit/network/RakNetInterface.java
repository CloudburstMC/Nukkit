package cn.nukkit.network;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.raknet.RakNet;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.raknet.protocol.packet.PING_DataPacket;
import cn.nukkit.raknet.server.RakNetServer;
import cn.nukkit.raknet.server.ServerHandler;
import cn.nukkit.raknet.server.ServerInstance;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RakNetInterface implements ServerInstance, AdvancedSourceInterface {

    private final Server server;

    private Network network;

    private final RakNetServer raknet;

    private final Map<String, Player> players = new ConcurrentHashMap<>();

    private final Map<String, Integer> networkLatency = new ConcurrentHashMap<>();

    private final Map<Integer, String> identifiers = new ConcurrentHashMap<>();

    private final Map<String, Integer> identifiersACK = new ConcurrentHashMap<>();

    private final ServerHandler handler;

    private int[] channelCounts = new int[256];

    public RakNetInterface(Server server) {
        this.server = server;

        this.raknet = new RakNetServer(this.server.getLogger(), this.server.getPort(), this.server.getIp().equals("") ? "0.0.0.0" : this.server.getIp());
        this.handler = new ServerHandler(this.raknet, this);
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        boolean work = false;
        if (this.handler.handlePacket()) {
            work = true;
            while (this.handler.handlePacket()) {

            }
        }

        return work;
    }

    @Override
    public void closeSession(String identifier, String reason) {
        if (this.players.containsKey(identifier)) {
            Player player = this.players.get(identifier);
            this.identifiers.remove(player.rawHashCode());
            this.players.remove(identifier);
            this.networkLatency.remove(identifier);
            this.identifiersACK.remove(identifier);
            player.close(player.getLeaveMessage(), reason);
        }
    }

    @Override
    public int getNetworkLatency(Player player) {
        return this.networkLatency.get(this.identifiers.get(player.rawHashCode()));
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        if (this.identifiers.containsKey(player.rawHashCode())) {
            String id = this.identifiers.get(player.rawHashCode());
            this.players.remove(id);
            this.networkLatency.remove(id);
            this.identifiersACK.remove(id);
            this.closeSession(id, reason);
            this.identifiers.remove(player.rawHashCode());
        }
    }

    @Override
    public void shutdown() {
        this.handler.shutdown();
    }

    @Override
    public void emergencyShutdown() {
        this.handler.emergencyShutdown();
    }

    @Override
    public void openSession(String identifier, String address, int port, long clientID) {
        PlayerCreationEvent ev = new PlayerCreationEvent(this, Player.class, Player.class, null, address, port);
        this.server.getPluginManager().callEvent(ev);
        Class<? extends Player> clazz = ev.getPlayerClass();

        try {
            Constructor constructor = clazz.getConstructor(SourceInterface.class, Long.class, String.class, int.class);
            Player player = (Player) constructor.newInstance(this, ev.getClientId(), ev.getAddress(), ev.getPort());
            this.players.put(identifier, player);
            this.networkLatency.put(identifier, 0);
            this.identifiersACK.put(identifier, 0);
            this.identifiers.put(player.rawHashCode(), identifier);
            this.server.addPlayer(identifier, player);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    @Override
    public void handleEncapsulated(String identifier, EncapsulatedPacket packet, int flags) {
        if (this.players.containsKey(identifier)) {
            DataPacket pk = null;
            try {
                if (packet.buffer.length > 0) {
                    if (packet.buffer[0] == PING_DataPacket.ID) {
                        PING_DataPacket pingPacket = new PING_DataPacket();
                        pingPacket.buffer = packet.buffer;
                        pingPacket.decode();

                        this.networkLatency.put(identifier, (int) pingPacket.pingID);
                        return;
                    }

                    pk = this.getPacket(packet.buffer);
                    if (pk != null) {
                        pk.decode();
                        this.players.get(identifier).handleDataPacket(pk);
                    }
                }
            } catch (Exception e) {
                this.server.getLogger().logException(e);
                if (Nukkit.DEBUG > 1 && pk != null) {
                    MainLogger logger = this.server.getLogger();
//                    if (logger != null) {
                    logger.debug("Packet " + pk.getClass().getName() + " 0x" + Binary.bytesToHexString(packet.buffer));
                    //logger.logException(e);
//                    }
                }

                if (this.players.containsKey(identifier)) {
                    this.handler.blockAddress(this.players.get(identifier).getAddress(), 5);
                }
            }
        }
    }

    @Override
    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    @Override
    public void blockAddress(String address, int timeout) {
        this.handler.blockAddress(address, timeout);
    }

    @Override
    public void unblockAddress(String address) {
        this.handler.unblockAddress(address);
    }

    @Override
    public void handleRaw(String address, int port, byte[] payload) {
        this.server.handlePacket(address, port, payload);
    }

    @Override
    public void sendRawPacket(String address, int port, byte[] payload) {
        this.handler.sendRaw(address, port, payload);
    }

    @Override
    public void notifyACK(String identifier, int identifierACK) {
        // TODO: Better ACK notification implementation!
        for (Player p : server.getOnlinePlayers().values()) {
            p.notifyACK(identifierACK);
        }
    }

    @Override
    public void setName(String name) {
        QueryRegenerateEvent info = this.server.getQueryInformation();
        String[] names = name.split("!@#");  //Split double names within the program
        this.handler.sendOption("name",
                "MCPE;" + Utils.rtrim(names[0].replace(";", "\\;"), '\\') + ";" +
                        ProtocolInfo.CURRENT_PROTOCOL + ";" +
                        ProtocolInfo.MINECRAFT_VERSION_NETWORK + ";" +
                        info.getPlayerCount() + ";" +
                        info.getMaxPlayerCount() + ";" +
                        this.server.getServerUniqueId().toString() + ";" +
                        (names.length > 1 ? Utils.rtrim(names[1].replace(";", "\\;"), '\\') : "") + ";" +
                        Server.getGamemodeString(this.server.getDefaultGamemode(), true) + ";");
    }

    public void setPortCheck(boolean value) {
        this.handler.sendOption("portChecking", String.valueOf(value));
    }

    @Override
    public void handleOption(String name, String value) {
        if ("bandwidth".equals(name)) {
            String[] v = value.split(";");
            this.network.addStatistics(Double.valueOf(v[0]), Double.valueOf(v[1]));
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet) {
        return this.putPacket(player, packet, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK) {
        return this.putPacket(player, packet, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate) {
        if (this.identifiers.containsKey(player.rawHashCode())) {
            byte[] buffer;
            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                buffer = ((BatchPacket) packet).payload;
            } else if (!needACK) {
                this.server.batchPackets(new Player[]{player}, new DataPacket[]{packet}, true);
                return null;
            } else {
                if (!packet.isEncoded) {
                    packet.encode();
                    packet.isEncoded = true;
                }
                buffer = packet.getBuffer();
                try {
                    buffer = Zlib.deflate(
                            Binary.appendBytes(Binary.writeUnsignedVarInt(buffer.length), buffer),
                            Server.getInstance().networkCompressionLevel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            String identifier = this.identifiers.get(player.rawHashCode());
            EncapsulatedPacket pk = null;
            if (!needACK) {
                if (packet.encapsulatedPacket == null) {
                    packet.encapsulatedPacket = new CacheEncapsulatedPacket();
                    packet.encapsulatedPacket.identifierACK = null;
                    packet.encapsulatedPacket.buffer = Binary.appendBytes((byte) 0xfe, buffer);
                    if (packet.getChannel() != 0) {
                        packet.encapsulatedPacket.reliability = 3;
                        packet.encapsulatedPacket.orderChannel = packet.getChannel();
                        packet.encapsulatedPacket.orderIndex = 0;
                    } else {
                        packet.encapsulatedPacket.reliability = 2;
                    }
                }
                pk = packet.encapsulatedPacket;
            }

            if (pk == null) {
                pk = new EncapsulatedPacket();
                pk.buffer = Binary.appendBytes((byte) 0xfe, buffer);
                if (packet.getChannel() != 0) {
                    packet.reliability = 3;
                    packet.orderChannel = packet.getChannel();
                    packet.orderIndex = 0;
                } else {
                    packet.reliability = 2;
                }

                if (needACK) {
                    int iACK = this.identifiersACK.get(identifier);
                    iACK++;
                    pk.identifierACK = iACK;
                    this.identifiersACK.put(identifier, iACK);
                }
            }

            this.handler.sendEncapsulated(identifier, pk, (needACK ? RakNet.FLAG_NEED_ACK : 0) | (immediate ? RakNet.PRIORITY_IMMEDIATE : RakNet.PRIORITY_NORMAL));

            return pk.identifierACK;
        }

        return null;
    }

    private DataPacket getPacket(byte[] buffer) {
        int start = 0;

        if (buffer[0] == (byte) 0xfe) {
            start++;
        }
        DataPacket data = this.network.getPacket(ProtocolInfo.BATCH_PACKET);

        if (data == null) {
            return null;
        }

        data.setBuffer(buffer, start);

        return data;
    }
}
