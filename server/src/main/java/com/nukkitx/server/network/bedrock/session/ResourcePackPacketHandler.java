package com.nukkitx.server.network.bedrock.session;

import com.nukkitx.api.resourcepack.BehaviorPack;
import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.event.player.PlayerInitializationEvent;
import com.nukkitx.server.resourcepack.loader.file.PackFile;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@Log4j2
public class ResourcePackPacketHandler implements BedrockPacketHandler {
    private final BedrockSession<NukkitPlayerSession> session;
    private final NukkitServer server;

    public ResourcePackPacketHandler(BedrockSession<NukkitPlayerSession> session, NukkitServer server) {
        this.session = session;
        this.server = server;
    }

    public void sendResourcePacksInfo() {
        ResourcePacksInfoPacket resourcePacksInfo = new ResourcePacksInfoPacket();
        // TODO: 14/01/2019 Specify required packs
        session.sendPacket(resourcePacksInfo);
    }

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(packet.getPackId());
        if (!optionalPack.isPresent()) {
            session.disconnect("disconnectionScreen.resourcePack");
            return true;
        }

        ResourcePack pack = optionalPack.get();
        ResourcePackChunkDataPacket chunkData = new ResourcePackChunkDataPacket();
        chunkData.setPackId(pack.getId());
        chunkData.setChunkIndex(packet.getChunkIndex());
        chunkData.setData(pack.getPackChunk(packet.getChunkIndex()));
        chunkData.setProgress(packet.getChunkIndex() * PackFile.CHUNK_SIZE);
        session.sendPacket(chunkData);
        return true;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        boolean forcePacks = server.getConfiguration().getGeneral().isForcingResourcePacks();
        switch (packet.getStatus()) {
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.setForcedToAccept(forcePacks);
                    /*if (server.getResourcePackManager().getResourceStack().length == 0) {
                        // We can skip the rest and go straight to start game however this appears to crash the game on occasions.
                        session.startGame();
                        return;
                    }*/
                for (String packId : packet.getPackIds()) {
                    String[] split = packId.split("_");
                    Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(UUID.fromString(split[0]));
                    if (!optionalPack.isPresent()) {
                        session.disconnect("disconnectionScreen.resourcePack");
                        return true;
                    }
                    ResourcePack pack = optionalPack.get();
                    if (pack instanceof BehaviorPack) {
                        stackPacket.getBehaviorPacks().add(new ResourcePackStackPacket.Entry(pack.getId(), pack.getVersion().toString(), ""));
                    } else {
                        stackPacket.getResourcePacks().add(new ResourcePackStackPacket.Entry(pack.getId(), pack.getVersion().toString(), ""));
                    }
                }
                session.sendPacket(stackPacket);
                break;
            case SEND_PACKS:
                for (String packId : packet.getPackIds()) {
                    String[] split = packId.split("_");
                    Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(UUID.fromString(split[0]));
                    if (!optionalPack.isPresent()) {
                        session.disconnect("disconnectionScreen.resourcePack");
                        return true;
                    }

                    ResourcePack pack = optionalPack.get();
                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.setPackId(pack.getId());
                    dataInfoPacket.setMaxChunkSize(PackFile.CHUNK_SIZE);
                    dataInfoPacket.setChunkCount(pack.getChunkCount());
                    dataInfoPacket.setCompressedPackSize(pack.getCompressedSize());
                    dataInfoPacket.setHash(pack.getSha256());
                    session.sendPacket(dataInfoPacket);
                }
                break;
            case NONE:
                break;
            case REFUSED:
                if (forcePacks) {
                    session.disconnect("disconnectionScreen.resourcePack");
                    break;
                }
                // Fall through
            case COMPLETED:
                initializePlayerSession();
                break;
            default:
                throw new IllegalStateException("Unknown resource pack status");
        }
        return true;
    }

    private void initializePlayerSession() {
        PlayerInitializationEvent event = new PlayerInitializationEvent(session, server.getDefaultLevel());

        NukkitPlayerSession player = event.getPlayerSession();
        if (player == null) {
            player = new NukkitPlayerSession(session, event.getLevel());
        }
        session.setPlayer(player);

        session.setHandler(player.getNetworkPacketHandler());

        if (!server.getSessionManager().add(session)) {
            throw new IllegalArgumentException("Player could not be added to SessionManager");
        }

        player.startGame();
    }
}
