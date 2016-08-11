package cn.nukkit.raknet.server;

import cn.nukkit.utils.ThreadedLogger;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UDPServerSocket {
    protected final ThreadedLogger logger;
    protected DatagramSocket socket;

    public UDPServerSocket(ThreadedLogger logger) {
        this(logger, 19132, "0.0.0.0");
    }

    public UDPServerSocket(ThreadedLogger logger, int port) {
        this(logger, port, "0.0.0.0");
    }

    public UDPServerSocket(ThreadedLogger logger, int port, String interfaz) {
        this.logger = logger;
        try {
            this.socket = new DatagramSocket(new InetSocketAddress(interfaz, port));
            this.socket.setReuseAddress(true);
            this.setSendBuffer(1024 * 1024 * 8).setRecvBuffer(1024 * 1024 * 8);
        } catch (IOException e) {
            this.logger.critical("**** FAILED TO BIND TO " + interfaz + ":" + port + "!");
            this.logger.critical("Perhaps a server is already running on that port?");
            System.exit(1);
        }
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void close() {
        this.socket.close();
    }

    public DatagramPacket readPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[65536], 65536);
        try {
            this.socket.receive(packet);
            packet.setData(Arrays.copyOf(packet.getData(), packet.getLength()));
            return packet;
        } catch (SocketTimeoutException exception) {
            return null;
        }
    }

    public int writePacket(byte[] data, String dest, int port) throws IOException {
        return this.writePacket(data, new InetSocketAddress(dest, port));
    }

    public int writePacket(byte[] data, InetSocketAddress dest) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, dest);
        this.socket.send(packet);
        return packet.getLength();
    }

    public UDPServerSocket setSendBuffer(int size) throws SocketException {
        socket.setSendBufferSize(size);
        return this;
    }

    public UDPServerSocket setRecvBuffer(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
        return this;
    }

}
