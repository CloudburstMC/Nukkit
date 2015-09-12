package cn.nukkit.raknet.server;

import cn.nukkit.raknet.protocol.Packet;
import cn.nukkit.utils.ThreadedLogger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SessionManager {
    protected Map<Byte, Class<? extends Packet>> packetPool = new ConcurrentHashMap<>();

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

    public SessionManager(RakNetServer server, UDPServerSocket socket) {
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

    public void run() {
        this.tickProcessor();
    }

    private void tickProcessor() {
        this.lastMeasure = System.currentTimeMillis();
        while (!this.shutdown) {
            long start = System.currentTimeMillis();
            int max = 5000;
        }
    }

    private void tick() {

    }

    private boolean receivePacket() {
        //todo alot
        return false;
    }

    public void sendPacket(Packet packet, String dest, int port) {

    }

    public void sendPacket(Packet packet, InetSocketAddress dest) {

    }

    private void registerPackets() {
        //todo alot
    }
}
