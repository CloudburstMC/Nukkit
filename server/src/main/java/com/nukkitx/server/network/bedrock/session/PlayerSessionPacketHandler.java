package com.nukkitx.server.network.bedrock.session;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.event.player.PlayerAnimationEvent;
import com.nukkitx.api.event.player.PlayerChatEvent;
import com.nukkitx.api.event.player.PlayerCommandPreprocessEvent;
import com.nukkitx.api.event.player.PlayerJoinEvent;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.message.ChatMessage;
import com.nukkitx.api.message.TextFormat;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.resourcepack.BehaviorPack;
import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.block.BlockUtil;
import com.nukkitx.server.block.behavior.BlockBehavior;
import com.nukkitx.server.block.behavior.BlockBehaviors;
import com.nukkitx.server.console.TranslatableMessage;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.component.PlayerDataComponent;
import com.nukkitx.server.inventory.NukkitInventory;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.packet.*;
import com.nukkitx.server.permission.NukkitAbilities;
import com.nukkitx.server.resourcepack.loader.file.PackFile;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@Log4j2
public class PlayerSessionPacketHandler implements NetworkPacketHandler {
    private final PlayerSession session;
    private final NukkitServer server;

    PlayerSessionPacketHandler(PlayerSession session) {
        this.session = session;
        this.server = session.getServer();
    }

    @Override
    public void handle(AdventureSettingsPacket packet) {
        PlayerDataComponent data = (PlayerDataComponent) session.ensureAndGet(PlayerData.class);

        if (data.getPlayerPermission() == PlayerPermission.OPERATOR) {
            NukkitAbilities abilities = data.getAbilities();
            abilities.setFlags(packet.getFlags());
            abilities.setFlags2(packet.getFlags2());
            //abilities.setCustomFlags(packet.getCustomFlags());
            data.setCommandPermission(packet.getCommandPermission());
            data.setPlayerPermission(packet.getPlayerPermission());
        }
        // TODO: Check that the player has permission to change these settings.
    }

    @Override
    public void handle(AnimatePacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }

        PlayerAnimationEvent event = new PlayerAnimationEvent(session, packet.getAction());
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return;
        }

        session.getLevel().getPacketManager().queuePacketForViewers(session, packet);
    }

    @Override
    public void handle(BlockEntityDataPacket packet) {

    }

    @Override
    public void handle(BlockPickRequestPacket packet) {

    }

    @Override
    public void handle(BookEditPacket packet) {

    }

    @Override
    public void handle(CommandBlockUpdatePacket packet) {

    }

    @Override
    public void handle(CommandRequestPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(session, packet.getCommand());
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return;
        }

        session.executeCommand(packet.getCommand().substring(1));
    }

    @Override
    public void handle(ContainerClosePacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }

        if (session.getOpenInventory() != null) {
            ((NukkitInventory) session.getOpenInventory()).getObservers().remove(session);
            session.setOpenInventory(null);
        }
    }

    @Override
    public void handle(CraftingEventPacket packet) {

    }

    @Override
    public void handle(EntityEventPacket packet) {

    }

    @Override
    public void handle(EntityPickRequestPacket packet) {

    }

    @Override
    public void handle(EventPacket packet) {

    }

    @Override
    public void handle(InteractPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }

        Optional<BaseEntity> entity = session.getLevel().getEntityManager().getEntityById(packet.getRuntimeEntityId());
        if (!entity.isPresent()) {
            return;
        }

        switch (packet.getAction()) {
            default:
                if (log.isDebugEnabled()) {
                    log.debug("Unhandled InteractPacket received from {} with action: {}", session.getName(), packet.getAction().name());
                }
                break;
        }
    }

    @Override
    public void handle(InventoryContentPacket packet) {

    }

    @Override
    public void handle(InventorySlotPacket packet) {

    }

    @Override
    public void handle(InventoryTransactionPacket packet) {
        packet.getTransaction().execute(session);
    }

    @Override
    public void handle(ItemFrameDropItemPacket packet) {

    }

    @Override
    public void handle(LevelSoundEventPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }
        session.getLevel().getPacketManager().queuePacketForViewers(packet.getPosition(), packet);
    }

    @Override
    public void handle(MapInfoRequestPacket packet) {

    }

    @Override
    public void handle(MobArmorEquipmentPacket packet) {

    }

    @Override
    public void handle(MobEquipmentPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }
        int hotbarSlot = packet.getHotbarSlot();
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent hotbar slot {}. Expected 0-8", session.getName(), hotbarSlot);
            }
            return;
        }

        int inventorySlot = packet.getInventorySlot();
        int slot = inventorySlot < 0 || inventorySlot >= session.getInventory().getInventoryType().getSize() ? -1 : inventorySlot;

        ItemInstance serverItem = session.getInventory().getItem(slot).orElse(BlockUtil.AIR);
        if (!serverItem.equals(packet.getItem())) {
            if (log.isDebugEnabled()) {
                log.debug("{} tried to equip {} but has {} in slot {}", session.getName(), packet.getItem(), serverItem, hotbarSlot);
            }
            session.sendPlayerInventory();
            return;
        }

        session.getInventory().setHotbarLink(hotbarSlot, slot);
        session.getInventory().setHeldHotbarSlot(hotbarSlot, false);
    }

    @Override
    public void handle(ModalFormResponsePacket packet) {

    }

    @Override
    public void handle(MoveEntityAbsolutePacket packet) {

    }

    @Override
    public void handle(MovePlayerPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }

        Vector3f oldPosition = session.getPosition();
        Vector3f newPosition = packet.getPosition().sub(0, session.getOffset(), 0);

        if (newPosition.distanceSquared(newPosition) >= 10000) {
            session.setPosition(oldPosition);
            session.setRotation(packet.getRotation());
            return;
        }

        session.setPosition(newPosition);
        session.setRotation(packet.getRotation());
        // If we haven't moved in the X or Z axis, don't update viewable entities or try updating chunks - they haven't changed.
        if (PlayerSession.hasSubstantiallyMoved(oldPosition, newPosition)) {
            session.updateViewableEntities();
            session.sendNewChunks().exceptionally(throwable -> {
                log.error("Unable to send chunks", throwable);
                session.disconnect("disconnect.disconnected");
                return null;
            });
        }
    }

    @Override
    public void handle(PhotoTransferPacket packet) {

    }

    @Override
    public void handle(PlayerActionPacket packet) {
        Damageable damageable = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || damageable.isDead()) {
            return;
        }
        PlayerData data = session.ensureAndGet(PlayerData.class);

        switch (packet.getAction()) {
            case START_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    return;
                }

                Optional<Block> block = session.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!block.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", session.getName(), packet.getBlockPosition());
                    }
                    return;
                }

                ItemInstance inHand = session.getInventory().getItemInHand().orElse(BlockUtil.AIR);

                BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.get().getBlockState().getBlockType());
                int breakTime = ((int) Math.ceil(blockBehavior.getBreakTime(session, block.get(), inHand))) * 5;
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_START_BREAK, (65534 + breakTime) / breakTime);
                return;
            case CONTINUE_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    return;
                }
                Optional<Block> blockBreakingOptional = session.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!blockBreakingOptional.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", session.getName(), packet.getBlockPosition());
                    }
                    return;
                }
                BlockState blockBreakingState = blockBreakingOptional.get().getBlockState();
                if (!blockBreakingState.getBlockType().isDiggable()) {
                    return;
                }
                int blockData = NukkitLevel.getPaletteManager().getOrCreateRuntimeId(blockBreakingState) | (packet.getFace().ordinal() << 24);
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.PARTICLE_PUNCH_BLOCK, blockData);
                return;
            case ABORT_BREAK:
            case STOP_BREAK:
                session.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_STOP_BREAK, 0);
                return;
            case GET_UPDATED_BLOCK:
            case DROP_ITEM:
            case START_SLEEP:
            case STOP_SLEEP:
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
    }

    @Override
    public void handle(PlayerHotbarPacket packet) {

    }

    @Override
    public void handle(PlayerInputPacket packet) {

    }

    @Override
    public void handle(PlayerSkinPacket packet) {

    }

    @Override
    public void handle(PurchaseReceiptPacket packet) {

    }

    @Override
    public void handle(RequestChunkRadiusPacket packet) {
        int radius = Math.max(5, Math.min(server.getConfiguration().getMechanics().getMaximumChunkRadius(), packet.getRadius()));
        ChunkRadiusUpdatePacket radiusPacket = new ChunkRadiusUpdatePacket();
        radiusPacket.setRadius(radius);
        session.getMinecraftSession().sendImmediatePackage(radiusPacket);
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
                session.getMinecraftSession().sendImmediatePackage(playStatus);

                session.sendMovePlayer();
                session.updateViewableEntities();

                //session.addToSendQueue(server.getCommandManager().createAvailableCommandsPacket(PlayerSession.this));

                TranslationMessage joinMessage = new TranslationMessage(TextFormat.YELLOW + "%multiplayer.player.joined", session.getName());
                log.info(TranslatableMessage.of(joinMessage));

                PlayerJoinEvent event = new PlayerJoinEvent(session, joinMessage);
                server.getEventManager().fire(event);

                event.getJoinMessage().ifPresent(server::broadcastMessage);
            }
        });

    }

    @Override
    public void handle(ResourcePackChunkRequestPacket packet) {
        Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(packet.getPackId());
        if (!optionalPack.isPresent()) {
            session.disconnect("disconnectionScreen.resourcePack");
            return;
        }

        ResourcePack pack = optionalPack.get();
        ResourcePackChunkDataPacket chunkData = new ResourcePackChunkDataPacket();
        chunkData.setPackId(pack.getId());
        chunkData.setChunkIndex(packet.getChunkIndex());
        chunkData.setData(pack.getPackChunk(packet.getChunkIndex()));
        chunkData.setProgress(packet.getChunkIndex() * PackFile.CHUNK_SIZE);
        session.getMinecraftSession().addToSendQueue(chunkData);
    }

    @Override
    public void handle(ResourcePackClientResponsePacket packet) {
        boolean forcePacks = server.getConfiguration().getGeneral().isForcingResourcePacks();
        switch (packet.getStatus()) {
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.setForcedToAccept(forcePacks);
                    /*if (server.getResourcePackManager().getResourceStack().length == 0) {
                        // We can skip the rest and go straight to start game.
                        session.startGame();
                        return;
                    }*/
                for (UUID id: packet.getPackIds()) {
                    Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(id);
                    if (!optionalPack.isPresent()) {
                        session.disconnect( "disconnectionScreen.resourcePack");
                        return;
                    }
                    ResourcePack pack = optionalPack.get();
                    if (pack instanceof BehaviorPack) {
                        stackPacket.getBehaviorPacks().add(pack);
                        continue;
                    }
                    stackPacket.getResourcePacks().add(pack);
                }
                session.getMinecraftSession().addToSendQueue(stackPacket);
                return;
            case SEND_PACKS:
                for (UUID packId: packet.getPackIds()) {
                    Optional<ResourcePack> optionalPack = server.getResourcePackManager().getPackById(packId);
                    if (!optionalPack.isPresent()) {
                        session.disconnect( "disconnectionScreen.resourcePack");
                        return;
                    }

                    ResourcePack pack = optionalPack.get();
                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.setPackId(pack.getId());
                    dataInfoPacket.setMaxChunkSize(PackFile.CHUNK_SIZE);
                    dataInfoPacket.setChunkCount(pack.getChunkCount());
                    dataInfoPacket.setCompressedPackSize(pack.getCompressedSize());
                    dataInfoPacket.setHash(pack.getSha256());
                    session.getMinecraftSession().addToSendQueue(dataInfoPacket);
                }
                return;
            case REFUSED:
                if (forcePacks) {
                    session.disconnect("disconnectionScreen.resourcePack");
                    return;
                }
                // Fall through
            case COMPLETED:
                session.startGame();
                break;
            default:
                throw new IllegalStateException("Unknown resource pack status");
        }
    }

    @Override
    public void handle(RiderJumpPacket packet) {

    }

    @Override
    public void handle(ServerSettingsRequestPacket packet) {

    }

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket packet) {
        //Player has spawned.
        session.setSpawned(true);

        session.updatePlayerList();
    }

    @Override
    public void handle(SetDefaultGameTypePacket packet) {

    }

    @Override
    public void handle(SetPlayerGameTypePacket packet) {

    }

    @Override
    public void handle(SubClientLoginPacket packet) {

    }

    @Override
    public void handle(TextPacket packet) {
        Damageable health = session.ensureAndGet(Damageable.class);
        if (!session.isSpawned() || health.isDead()) {
            return;
        }

        if (!(packet.getMessage() instanceof ChatMessage)) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent {} when only ChatMessages are allowed", session.getName(), packet.getMessage().getClass().getName());
            }
            return;
        }
        ChatMessage message = (ChatMessage) packet.getMessage();
        String messageString = message.getMessage().trim();
        if (messageString.isEmpty() || messageString.contains("\0")) {
            return;
        }

        if (message.getMessage().startsWith("/")) {
            // Command
            String command = messageString.substring(1);
            session.executeCommand(command);
        }

        PlayerChatEvent event = new PlayerChatEvent(session, message);
        if (event.isCancelled()) {
            return;
        }

        packet.setXuid(session.getXuid().orElse(""));
        packet.setMessage(new ChatMessage(session.getName(), messageString, false)); // To stop any name forgery.
        session.getLevel().getPacketManager().queuePacketForPlayers(packet);
    }
}
