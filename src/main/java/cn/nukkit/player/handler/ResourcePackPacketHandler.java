package cn.nukkit.player.handler;

import cn.nukkit.Server;
import cn.nukkit.pack.Pack;
import cn.nukkit.player.PlayerLoginData;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import lombok.extern.log4j.Log4j2;

/**
 * @author Extollite
 */
@Log4j2
public class ResourcePackPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
    private final Server server;

    private PlayerLoginData loginData;

    public ResourcePackPacketHandler(BedrockServerSession session, Server server, PlayerLoginData loginData) {
        this.session = session;
        this.server = server;
        this.loginData = loginData;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case REFUSED:
                session.disconnect("disconnectionScreen.noReason");
                return true;
            case SEND_PACKS:
                for (String entry : packet.getPackIds()) {
                    Pack pack = this.server.getPackManager().getPackByIdVersion(entry);
                    if (pack == null) {
                        session.disconnect("disconnectionScreen.resourcePack");
                        return true;
                    }

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.setPackId(pack.getId());
                    dataInfoPacket.setPackVersion(pack.getVersion().toString());
                    dataInfoPacket.setMaxChunkSize(1048576); //megabyte
                    dataInfoPacket.setChunkCount(pack.getSize() / dataInfoPacket.getMaxChunkSize());
                    dataInfoPacket.setCompressedPackSize(pack.getSize());
                    dataInfoPacket.setHash(pack.getHash());
                    dataInfoPacket.setType(pack.getType());
                    session.sendPacket(dataInfoPacket);
                }
                return true;
            case HAVE_ALL_PACKS:
                session.sendPacket(this.server.getPackManager().getPackStack());
                return true;
            case COMPLETED:
                if (loginData.getPreLoginEventTask().isFinished()) {
                    loginData.initializePlayer();
                } else {
                    loginData.setShouldLogin(true);
                }
                return true;
        }
        return true;
    }

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        Pack resourcePack = this.server.getPackManager().getPackByIdVersion(
                packet.getPackId() + "_" + packet.getPackVersion());
        if (resourcePack == null) {
            session.disconnect("disconnectionScreen.resourcePack");
            return true;
        }

        ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
        dataPacket.setPackId(packet.getPackId());
        dataPacket.setPackVersion(packet.getPackVersion());
        dataPacket.setChunkIndex(packet.getChunkIndex());
        dataPacket.setData(resourcePack.getChunk(1048576 * packet.getChunkIndex(), 1048576));
        dataPacket.setProgress(1048576 * packet.getChunkIndex());
        session.sendPacket(dataPacket);
        return true;
    }

}
