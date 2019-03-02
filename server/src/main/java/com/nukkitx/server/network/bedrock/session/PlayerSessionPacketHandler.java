package com.nukkitx.server.network.bedrock.session;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.event.player.*;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.message.ChatMessage;
import com.nukkitx.api.message.TextFormat;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.api.permission.CommandPermission;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.block.behavior.BlockBehavior;
import com.nukkitx.server.block.behavior.BlockBehaviors;
import com.nukkitx.server.console.TranslatableMessage;
import com.nukkitx.server.container.NukkitContainer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.component.PlayerDataComponent;
import com.nukkitx.server.inventory.transaction.InventoryTransaction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.inventory.transaction.InventoryTransactions;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.permission.NukkitAbilities;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class PlayerSessionPacketHandler implements BedrockPacketHandler {
    private final NukkitPlayerSession player;
    private final NukkitServer server;

    PlayerSessionPacketHandler(NukkitPlayerSession session) {
        this.player = session;
        this.server = session.getServer();
    }

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        PlayerDataComponent data = (PlayerDataComponent) player.ensureAndGet(PlayerData.class);

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
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }

        Player.Animation action = Player.Animation.valueOf(packet.getAction().name());
        PlayerAnimationEvent event;
        if (action == Player.Animation.ROW_LEFT || action == Player.Animation.ROW_RIGHT) {
            event = new PlayerRowAnimationEvent(player, action, packet.getRowingTime());
        } else {
            event = new PlayerAnimationEvent(player, action);
        }
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return true;
        }

        player.getLevel().getPacketManager().queuePacketForViewers(player, packet);
        return true;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, packet.getCommand());
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return true;
        }

        player.executeCommand(packet.getCommand().substring(1));
        return true;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }


        player.getOpenContainer().ifPresent(container -> {
            NukkitContainer nukkitContainer = (NukkitContainer) container;
            nukkitContainer.removeContentListener(player);
            //nukkitContainer.removeSizeListener(player);
        });
        return true;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }

        Optional<BaseEntity> entity = player.getLevel().getEntityManager().getEntityById(packet.getRuntimeEntityId());
        if (!entity.isPresent()) {
            return true;
        }

        switch (packet.getAction()) {
            default:
                if (log.isDebugEnabled()) {
                    log.debug("Unhandled InteractPacket received from {} with action: {}", player.getName(), packet.getAction());
                }
                break;
        }
        return true;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        InventoryTransaction transaction = InventoryTransactions.fromPacket(packet);
        InventoryTransactionResult result = transaction.handle(player, false);

        if (result != InventoryTransactionResult.SUCCESS) {
            transaction.onTransactionError(player, result);
        }
        return true;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }
        player.getLevel().getPacketManager().queuePacketForViewers(packet.getPosition(), packet);
        return true;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }
        int hotbarSlot = packet.getHotbarSlot();
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent hotbar slot {}. Expected 0-8", player.getName(), hotbarSlot);
            }
            return true;
        }

        int inventorySlot = packet.getInventorySlot();
        int slot = inventorySlot < 0 || inventorySlot >= player.getInventory().getSize() ? -1 : inventorySlot;

        ItemStack clientItem = ItemUtils.fromNetwork(packet.getItem());
        ItemStack serverItem = player.getInventory().getSlot(slot);
        if (!serverItem.equals(clientItem)) {
            if (log.isDebugEnabled()) {
                log.debug("{} tried to equip {} but has {} in slot {}", player.getName(), packet.getItem(), serverItem, hotbarSlot);
            }
            player.getDispatcher().sendInventory(true);
            return true;
        }

        player.getInventory().selectSlot(hotbarSlot, packet.getContainerId());
        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }

        Vector3f oldPosition = player.getPosition();
        Vector3f newPosition = packet.getPosition().sub(0, player.getOffset(), 0);

        if (newPosition.distanceSquared(newPosition) >= 10000) {
            player.setPosition(oldPosition);
            player.setRotation(Rotation.from(packet.getRotation()));
            return true;
        }

        player.setPosition(newPosition);
        player.setRotation(Rotation.from(packet.getRotation()));
        // If we haven't moved in the X or Z axis, don't update viewable entities or try updating chunks - they haven't changed.
        if (NukkitPlayerSession.hasSubstantiallyMoved(oldPosition, newPosition)) {
            player.updateViewableEntities();
            player.sendNewChunks().exceptionally(throwable -> {
                log.error("Unable to send chunks", throwable);
                player.disconnect("disconnect.disconnected");
                return null;
            });
        }
        return true;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        Damageable damageable = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || damageable.isDead()) {
            return true;
        }
        PlayerData data = player.ensureAndGet(PlayerData.class);

        switch (packet.getAction()) {
            case START_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    break;
                }

                Optional<Block> block = player.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!block.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", player.getName(), packet.getBlockPosition());
                    }
                    break;
                }

                ItemStack inHand = player.getInventory().getSelectedItem();

                BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.get().getBlockState().getBlockType());
                int breakTime = ((int) Math.ceil(blockBehavior.getBreakTime(player, block.get(), inHand))) * 5;
                player.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_START_BREAK, (65534 + breakTime) / breakTime);
                break;
            case CONTINUE_BREAK:
                if (data.getGameMode() != GameMode.SURVIVAL) {
                    break;
                }
                Optional<Block> blockBreakingOptional = player.getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                if (!blockBreakingOptional.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} attempted to break an unloaded block at {}", player.getName(), packet.getBlockPosition());
                    }
                    break;
                }
                BlockState blockBreakingState = blockBreakingOptional.get().getBlockState();
                if (!blockBreakingState.getBlockType().isDiggable()) {
                    break;
                }
                int blockData = NukkitLevel.getPaletteManager().getOrCreateRuntimeId(blockBreakingState) | (packet.getFace() << 24);
                player.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.PARTICLE_PUNCH_BLOCK, blockData);
                break;
            case ABORT_BREAK:
            case STOP_BREAK:
                player.getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_STOP_BREAK, 0);
                break;
            case GET_UPDATED_BLOCK:
            case DROP_ITEM:
            case START_SLEEP:
            case STOP_SLEEP:
                break;
            case RESPAWN:
                player.sendHealth();
            case JUMP:
            case START_SPRINT:
                data.setSprinting(true);
                break;
            case STOP_SPRINT:
                data.setSprinting(false);
                break;
            case START_SNEAK:
                player.setSneaking(true);
            case STOP_SNEAK:
                player.setSneaking(false);
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
        //player.sendNetworkPacket(radiusPacket);
        player.setViewDistance(radius);

        player.sendNewChunks().whenComplete((chunks, throwable) -> {
            if (throwable != null) {
                log.error("Unable to load chunks", throwable);
                player.disconnect();
                return;
            }
            // If the player has not spawned, we need to start the spawning sequence.
            if (!player.isSpawned()) {
                PlayStatusPacket playStatus = new PlayStatusPacket();
                playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
                player.sendNetworkPacket(playStatus);
                player.sendEntityData();
                player.sendAdventureSettings();

                //player.sendNetworkPacket(server.getCommandManager().createAvailableCommandsPacket(NukkitPlayerSession.this));
            }
        });
        return true;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        //Player has spawned.
        player.setSpawned(true);

        TranslationMessage joinMessage = new TranslationMessage(TextFormat.YELLOW + "%multiplayer.player.joined", player.getName());
        log.info(TranslatableMessage.of(joinMessage));

        PlayerJoinEvent event = new PlayerJoinEvent(player, joinMessage);
        server.getEventManager().fire(event);

        event.getJoinMessage().ifPresent(server::broadcastMessage);
        return true;
    }

    @Override
    public boolean handle(TextPacket packet) {
        Damageable health = player.ensureAndGet(Damageable.class);
        if (!player.isSpawned() || health.isDead()) {
            return true;
        }

        if (packet.getType() != TextPacket.Type.CHAT) {
            if (log.isDebugEnabled()) {
                log.debug("{} sent {} when only ChatMessages are allowed", player.getName(), packet.getType());
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
            player.executeCommand(command);
        }

        PlayerChatEvent event = new PlayerChatEvent(player, message);
        if (event.isCancelled()) {
            return true;
        }

        packet.setXuid(player.getXuid().orElse(""));
        packet.setSourceName(player.getName()); // Stop any name forgery.
        packet.setNeedsTranslation(false);
        player.getLevel().getPacketManager().queuePacketForPlayers(packet);
        return true;
    }
}
