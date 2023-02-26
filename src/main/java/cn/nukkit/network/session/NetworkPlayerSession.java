package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.network.CompressionProvider;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public interface NetworkPlayerSession {

    void sendPacket(BedrockPacket packet);
    void sendImmediatePacket(BedrockPacket packet, Runnable callback);

    void disconnect(String reason);

    Player getPlayer();

    void setCompression(CompressionProvider compression);
    CompressionProvider getCompression();
}
