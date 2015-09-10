package cn.nukkit.raknet.server;

import cn.nukkit.raknet.UDPServerSocket;
import cn.nukkit.utils.ThreadedLogger;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RakNetServer extends Thread {
    protected int port;
    protected String interfaz;

    protected ThreadedLogger logger;

    protected final LinkedList<byte[]> externalQueue;
    protected final LinkedList<byte[]> internalQueue;


    protected boolean shutdown;


    public RakNetServer(ThreadedLogger logger, int port) {
        this(logger, port, "0.0.0.0");
    }

    public RakNetServer(ThreadedLogger logger, int port, String interfaz) {
        this.port = port;
        if (port < 1 || port > 65536) {
            throw new IllegalArgumentException("Invalid port range");
        }

        this.interfaz = interfaz;
        this.logger = logger;

        this.externalQueue = new LinkedList<>();
        this.internalQueue = new LinkedList<>();

        this.start();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public int getPort() {
        return port;
    }

    public String getInterface() {
        return interfaz;
    }

    public ThreadedLogger getLogger() {
        return logger;
    }

    public LinkedList<byte[]> getExternalQueue() {
        return externalQueue;
    }

    public LinkedList<byte[]> getInternalQueue() {
        return internalQueue;
    }

    public void pushMainToThreadPacket(byte[] data) {
        synchronized (this.internalQueue) {
            this.internalQueue.add(data);
        }

    }

    public byte[] readMainToThreadPacket() {
        synchronized (this.internalQueue) {
            return this.internalQueue.poll();
        }
    }

    public void pushThreadToMainPacket(byte[] data) {
        synchronized (this.externalQueue) {
            this.externalQueue.add(data);
        }

    }

    public byte[] readThreadToMainPacket() {
        synchronized (this.externalQueue) {
            return this.externalQueue.poll();
        }
    }

    private class ShutdownHandler extends Thread {
        public void run() {
            if (!shutdown) {
                logger.emergency("[RakNet Thread #" + Thread.currentThread().getId() + "] RakNet crashed!");
            }
        }
    }

    @Override
    public void run() {
        this.setName("RakNet Thread #" + Thread.currentThread().getId());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
        UDPServerSocket socket = new UDPServerSocket(this.getLogger(), port, this.interfaz);
        
    }
}
