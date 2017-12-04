package cn.nukkit.server.network;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface AdvancedSourceInterface extends SourceInterface {

    void blockAddress(String address);

    void blockAddress(String address, int timeout);

    void unblockAddress(String address);

    void setNetwork(Network network);

    void sendRawPacket(String address, int port, byte[] payload);
}
