package com.nukkitx.server.network.bedrock.session;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.event.player.*;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.message.ChatMessage;
import com.nukkitx.api.message.TextFormat;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.api.permission.CommandPermission;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.resourcepack.BehaviorPack;
import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.block.BlockUtil;
import com.nukkitx.server.block.behavior.BlockBehavior;
import com.nukkitx.server.block.behavior.BlockBehaviors;
import com.nukkitx.server.console.TranslatableMessage;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.component.PlayerDataComponent;
import com.nukkitx.server.inventory.NukkitInventory;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.permission.NukkitAbilities;
import com.nukkitx.server.resourcepack.loader.file.PackFile;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@Log4j2
public class PlayerSessionPacketHandler implements BedrockPacketHandler {
    private final NukkitPlayerSession session;
    private final NukkitServer server;

    PlayerSessionPacketHandler(NukkitPlayerSession session) {
        this.session = session;
        this.server = session.getServer();
    }

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        PlayerDataComponent data = (PlayerDataComponent) session.ensureAndGet(PlayerData.class);

        if (data.getPlayerPermission() == PlayerPermission.OPERATOR) {
            NukkitAbilities abilities = data.getAbilities();
            abilities.setFlags(packet.getPlayerFlags());
            abilities.setFlags2(packet.getWorldFlags());
            //abilities.setCustomFlags(packet.getCustomFlags());
            data.setCommandPermission(CommandPermission.values()[packet.getCommandPermission()]);
            data.setPlayerPermission(PlayerPermission.values()[packet.getPlayerPermission()]);
        }
        // TODO: Check that the player has permission to change these settings.
        return true;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }

        Player.Animation action = Player.Animation.valueOf(packet.getAction().name());
        PlayerAnimationEvent event;
        if (action == Player.Animation.ROW_LEFT || action == Player.Animation.ROW_RIGHT) {
            event = new PlayerRowAnimationEvent(session, action, packet.getRowingTime());
        } else {
            event = new PlayerAnimationEvent(session, action);
        }
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return true;
        }

        session.getLevel().getPacketManager().queuePacketForViewers(session, packet);
        return true;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(session, packet.getCommand());
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return true;
        }

        session.executeCommand(packet.getCommand().substring(1));
        return true;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }

        if (session.getOpenInventory() != null) {
            ((NukkitInventory) session.getOpenInventory()).getObservers().remove(session);
            session.setOpenInventory(null);
        }
        return true;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }

        Optional<BaseEntity> entity = session.getLevel().getEntityManager().getEntityById(packet.getRuntimeEntityId());
        if (!entity.isPresent()) {
            return true;
        }

        switch (packet.getAction()) {
            default:
                if (log.isDebugEnabled()) {
                    log.debug("Unhandled InteractPacket received from {} with action: {}", session.getName(), packet.getAction());
                }
                break;
        }
        return true;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }
        session.getLevel().getPacketManager().queuePacketForViewers(packet.getPosition(), packet);
        return true;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }
        int hotbarSlot = packet.getHotbarSlot();
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent hotbar slot {}. Expected 0-8", session.getName(), hotbarSlot);
            }
            return true;
        }

        int inventorySlot = packet.getInventorySlot();
        int slot = inventorySlot < 0 || inventorySlot >= session.getInventory().getInventoryType().getSize() ? -1 : inventorySlot;

        ItemInstance serverItem = session.getInventory().getItem(slot).orElse(BlockUtil.AIR);
        if (!serverItem.equals(packet.getItem())) {
            if (log.isDebugEnabled()) {
                log.debug("{} tried to equip {} but has {} in slot {}", session.getName(), packet.getItem(), serverItem, hotbarSlot);
            }
            session.sendPlayerInventory();
            return true;
        }

        session.getInventory().setHotbarLink(hotbarSlot, slot);
        session.getInventory().setHeldHotbarSlot(hotbarSlot, false);
        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }

        Vector3f oldPosition = session.getPosition();
        Vector3f newPosition = packet.getPosition().sub(0, session.getOffset(), 0);

        if (newPosition.distanceSquared(newPosition) >= 10000) {
            session.setPosition(oldPosition);
            session.setRotation(Rotation.from(packet.getRotation()));
            return true;
        }

        session.setPosition(newPosition);
        session.setRotation(Rotation.from(packet.getRotation()));
        // If we haven't moved in the X or Z axis, don't update viewable entities or try updating chunks - they haven't changed.
        if (NukkitPlayerSession.hasSubstantiallyMoved(oldPosition, newPosition)) {
            session.updateViewableEntities();
            session.sendNewChunks().exceptionally(throwable -> {
                log.error("Unable to send chunks", throwable);
                session.disconnect("disconnect.disconnected");
                return null;
            });
        }
        return true;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return true;
        }
        PlayerData data = session.ensureAndGet(PlayerData.class);

        switch (packet.getAction()) {
            case START_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    break;
                }

                Optional<Block> block = session.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!block.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", session.getName(), packet.getBlockPosition());
                    }
                    break;
                }

                ItemInstance inHand = session.getInventory().getItemInHand().orElse(BlockUtil.AIR);

                BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.get().getBlockState().getBlockType());
                int breakTime = ((int) Math.ceil(blockBehavior.getBreakTime(session, block.get(), inHand))) * 5;
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_START_BREAK, (65534 + breakTime) / breakTime);
                break;
            case CONTINUE_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    break;
                }
                Optional<Block> blockBreakingOptional = session.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!blockBreakingOptional.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", session.getName(), packet.getBlockPosition());
                    }
                    break;
                }
                BlockState blockBreakingState = blockBreakingOptional.get().getBlockState();
                if (!blockBreakingState.getBlockType().isDiggable()) {
                    break;
                }
                int blockData = NukkitLevel.getPaletteManager().getOrCreateRuntimeId(blockBreakingState) | (packet.getFace() << 24);
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.PARTICLE_PUNCH_BLOCK, blockData);
                break;
            case ABORT_BREAK:
            case STOP_BREAK:
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_STOP_BREAK, 0);
                break;
            case GET_UPDATED_BLOCK:
            case DROP_ITEM:
            case START_SLEEP:
            case STOP_SLEEP:
                break;
            case RESPAWN:
                session.sendHealth();
            case JUMP:
            case START_SPRINT:
                data.setSprinting(true);
                break;
            case STOP_SPRINT:
                data.setSprinting(false);
                break;
            case START_SNEAK:
                data.setSneaking(true);
            case STOP_SNEAK:
                data.setSneaking(false);
            case DIMENSION_CHANGE_REQUEST:
            case DIMENSION_CHANGE_SUCCESS:
            case START_GLIDE:
            case STOP_GLIDE:
            case BUILD_DENIED:
            case CHANGE_SKIN:
            case SET_ENCHANTMENT_SEED:
        }
        return true;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        int radius = Math.max(5, Math.min(server.getConfiguration().getMechanics().getMaximumChunkRadius(), packet.getRadius()));
        ChunkRadiusUpdatedPacket radiusPacket = new ChunkRadiusUpdatedPacket();
        radiusPacket.setRadius(radius);
        session.getBedrockSession().sendPacket(radiusPacket);
        session.setViewDistance(radius);

        session.sendNewChunks().whenComplete((chunks, throwable) -> {
            if (throwable != null) {
                log.error("Unable to load chunks", throwable);
                session.disconnect();
                return;
            }
            // If the player has not spawned, we need to start the spawning sequence.
            if (!session.isSpawned()) {
                session.sendEntityData();

                PlayStatusPacket playStatus = new PlayStatusPacket();
                playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
                session.getBedrockSession().sendPacket(playStatus);

                session.sendMovePlayer();
                session.updateViewableEntities();

                //session.sendPacket(server.getCommandManager().createAvailableCommandsPacket(NukkitPlayerSession.this));
            }
        });
        return true;
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
        session.getBedrockSession().sendPacket(chunkData);
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
                        session.disconnect( "disconnectionScreen.resourcePack");
                        return true;
                    }
                    ResourcePack pack = optionalPack.get();
                    if (pack instanceof BehaviorPack) {
                        stackPacket.getBehaviorPacks().add(new ResourcePackStackPacket.Entry(pack.getId(), pack.getVersion().toString(), ""));
                    } else {
                        stackPacket.getResourcePacks().add(new ResourcePackStackPacket.Entry(pack.getId(), pack.getVersion().toString(), ""));
                    }
                }
                session.getBedrockSession().sendPacket(stackPacket);
                break;
            case SEND_PACKS:
                for (String packId : packet.getPackIds()) {
                    String[] split = packId.split("_");
                    Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(UUID.fromString(split[0]));
                    if (!optionalPack.isPresent()) {
                        session.disconnect( "disconnectionScreen.resourcePack");
                        return true;
                    }

                    ResourcePack pack = optionalPack.get();
                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.setPackId(pack.getId());
                    dataInfoPacket.setMaxChunkSize(PackFile.CHUNK_SIZE);
                    dataInfoPacket.setChunkCount(pack.getChunkCount());
                    dataInfoPacket.setCompressedPackSize(pack.getCompressedSize());
                    dataInfoPacket.setHash(pack.getSha256());
                    session.getBedrockSession().sendPacket(dataInfoPacket);
                }
                break;
            case REFUSED:
                if (forcePacks) {
                    session.disconnect("disconnectionScreen.resourcePack");
                    break;
                }
                // Fall through
            case COMPLETED:
                session.startGame();
                break;
            default:
                throw new IllegalStateException("Unknown resource pack status");
        }
        return true;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        //Player has spawned.
        session.setSpawned(true);

        session.updatePlayerList();

        TranslationMessage joinMessage = new TranslationMessage(TextFormat.YELLOW + "%multiplayer.player.joined", session.getName());
        log.info(TranslatableMessage.of(joinMessage));

        PlayerJoinEvent event = new PlayerJoinEvent(session, joinMessage);
        server.getEventManager().fire(event);

        event.getJoinMessage().ifPresent(server::broadcastMessage);
        return true;
    }

    @Override
    public boolean handle(TextPacket packet) {
        Damageable health = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || health.isDead()) {
            return true;
        }

        if (packet.getType() != TextPacket.Type.CHAT) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent {} when only ChatMessages are allowed", session.getName(), packet.getType());
            }
            return true;
        }
        ChatMessage message = new ChatMessage(packet.getSourceName(), packet.getMessage(), packet.isNeedsTranslation());
        String messageString = packet.getMessage().trim();
        if (messageString.isEmpty() || messageString.contains("\0")) {
            return true;
        }

        if (packet.getMessage().charAt(0) == '/') {
            // Command
            String command = messageString.substring(1);
            session.executeCommand(command);
        }

        PlayerChatEvent event = new PlayerChatEvent(session, message);
        if (event.isCancelled()) {
            return true;
        }

        packet.setXuid(session.getXuid().orElse(""));
        packet.setSourceName(session.getName()); // Stop any name forgery.
        packet.setNeedsTranslation(false);
        session.getLevel().getPacketManager().queuePacketForPlayers(packet);
        return true;
    }
}
