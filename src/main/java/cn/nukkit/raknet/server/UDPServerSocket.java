package cn.nukkit.raknet.server;

import cn.nukkit.utils.ThreadedLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UDPServerSocket {

    protected ThreadedLogger logger;
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
        } catch (SocketException e) {
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
        DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);

        this.socket.receive(packet);
        packet.setData(Arrays.copyOf(packet.getData(), packet.getLength()));
        return packet;
    }

    public void writePacket(byte[] data, String dest, int port) throws IOException {
        this.writePacket(data, new InetSocketAddress(dest, port));
    }

    public void writePacket(byte[] data, InetSocketAddress dest) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, dest);
        this.socket.send(packet);
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
