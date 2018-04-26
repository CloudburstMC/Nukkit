package cn.nukkit.server.network.minecraft.session;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.command.CommandException;
import cn.nukkit.api.command.CommandNotFoundException;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.PlayerData;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;
import cn.nukkit.api.event.player.*;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.message.ChatMessage;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.message.RawMessage;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionAttachment;
import cn.nukkit.api.permission.PermissionAttachmentInfo;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.resourcepack.BehaviorPack;
import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.api.util.Skin;
import cn.nukkit.api.util.data.DeviceOS;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.block.BlockUtil;
import cn.nukkit.server.block.behavior.BlockBehavior;
import cn.nukkit.server.block.behavior.BlockBehaviors;
import cn.nukkit.server.console.TranslatableMessage;
import cn.nukkit.server.entity.Attribute;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.entity.component.DamageableComponent;
import cn.nukkit.server.entity.component.PlayerDataComponent;
import cn.nukkit.server.inventory.InventoryObserver;
import cn.nukkit.server.inventory.NukkitInventory;
import cn.nukkit.server.inventory.NukkitInventoryType;
import cn.nukkit.server.inventory.NukkitPlayerInventory;
import cn.nukkit.server.inventory.transaction.*;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.chunk.FullChunkDataPacketCreator;
import cn.nukkit.server.level.util.AroundPointChunkComparator;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.data.MetadataConstants;
import cn.nukkit.server.network.minecraft.packet.*;
import cn.nukkit.server.network.minecraft.util.MetadataDictionary;
import cn.nukkit.server.permission.NukkitAbilities;
import cn.nukkit.server.resourcepack.loader.file.PackFile;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.spotify.futures.CompletableFutures;
import gnu.trove.TCollections;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class PlayerSession extends LivingEntity implements Player, InventoryObserver {
    private final MinecraftSession session;
    private final NukkitServer server;
    private final NukkitLevel level;
    private final TLongSet viewableEntities = new TLongHashSet();
    private final TLongSet hiddenEntities = TCollections.synchronizedSet(new TLongHashSet());
    private final TLongSet sentChunks = TCollections.synchronizedSet(new TLongHashSet());
    private final AtomicReference<Locale> locale = new AtomicReference<>();
    private boolean commandsEnabled = true;
    private boolean hasMoved;
    @Getter
    private final NukkitPlayerInventory inventory = new NukkitPlayerInventory(this);
    @Getter
    private Inventory openInventory;
    private int enchantmentSeed;
    private int viewDistance;

    private boolean spawned = false;

    public PlayerSession(MinecraftSession session, NukkitLevel level) {
        super(EntityType.PLAYER, level.getData().getDefaultSpawn(), level, session.getServer(), 20);
        this.level = level;
        this.session = session;
        this.server = session.getServer();
        Path playerDatPath = server.getPlayersPath().resolve(getXuid().isPresent() ? getUniqueId().toString() : getName());
        locale.set(server.getLocaleManager().getLocaleByString(session.getClientData().getLanguageCode()));

        enchantmentSeed = ThreadLocalRandom.current().nextInt();
        viewDistance = server.getConfiguration().getMechanics().getViewDistance();

        // Register components
        registerComponent(PlayerData.class, new PlayerDataComponent(this));
    }

    public MinecraftSession getMinecraftSession() {
        return session;
    }

    public boolean isChunkInView(int x, int z) {
        return sentChunks.contains(getChunkKey(x, z));
    }

    public CompletableFuture<List<Chunk>> sendNewChunks() {
        return getChunksForRadius(viewDistance).whenComplete(((chunks, throwable) -> {
            if (throwable != null) {
                log.error("Cannot load chunks for" + session.getAuthData().getDisplayName());
                session.disconnect("Internal Error");
                return;
            }

            // Send closest chunks first.
            Vector3f position = getPosition();
            int chunkX = position.getFloorX() >> 4;
            int chunkZ = position.getFloorZ() >> 4;

            chunks.sort(new AroundPointChunkComparator(chunkX, chunkZ));

            for (Chunk chunk : chunks) {
                // Already wrapped so we can send immediately.
                session.sendImmediatePackage(((FullChunkDataPacketCreator) chunk).createFullChunkDataPacket());
            }
        }));
    }

    public void updateViewableEntities() {
        synchronized (viewableEntities) {
            Collection<BaseEntity> inView = level.getEntityManager().getEntitiesInDistance(getPosition(), server.getConfiguration().getMechanics().getViewDistance());
            TLongSet toRemove = new TLongHashSet();
            Collection<BaseEntity> toAdd = new ArrayList<>();

            // Remove entities out of range
            viewableEntities.forEach(id -> {
                Optional<BaseEntity> optionalEntity = level.getEntityManager().getEntityById(id);
                if (optionalEntity.isPresent()) {
                    if (!inView.contains(optionalEntity.get())) {
                        toRemove.add(id);
                    }
                    if (hiddenEntities.contains(id)) {
                        toRemove.add(id);
                    }
                } else {
                    toRemove.add(id);
                }
                return true;
            });

            for (BaseEntity entity : inView) {
                if (entity.getEntityId() == getEntityId()) {
                    continue; // Can't add our self.
                }

                long chunkKey = getChunkKey(entity.getPosition().getFloorX() >> 4, entity.getPosition().getFloorZ() >> 4);
                if (sentChunks.contains(chunkKey) && viewableEntities.add(entity.getEntityId()) && !hiddenEntities.contains(entity.getEntityId())) {
                    toAdd.add(entity);
                }
            }

            viewableEntities.removeAll(toRemove);

            toRemove.forEach(id -> {
                RemoveEntityPacket packet = new RemoveEntityPacket();
                packet.setUniqueEntityId(id);
                session.addToSendQueue(packet);
                return true;
            });

            for (BaseEntity entity : toAdd) {
                session.addToSendQueue(entity.createAddEntityPacket());
            }
        }
    }

    public void save() {
    }

    public CompletableFuture<List<Chunk>> getChunksForRadius(int radius) {
        int chunkX = getPosition().getFloorX() >> 4;
        int chunkZ = getPosition().getFloorZ() >> 4;

        TLongSet chunksForRadius = new TLongHashSet();
        List<CompletableFuture<Chunk>> chunkFutures = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int cx = chunkX + x;
                int cz = chunkZ + z;
                long key = getChunkKey(cx, cz);

                chunksForRadius.add(key);
                if (!sentChunks.add(key)) {
                    continue;
                }

                chunkFutures.add(getLevel().getChunk(cx, cz));
            }
        }

        sentChunks.retainAll(chunksForRadius);

        return CompletableFutures.allAsList(chunkFutures);
    }

    private void startGame() {
        PlayerLoginEvent event = new PlayerLoginEvent(this, level.getData().getDefaultSpawn(), level, getRotation(), level.getData().getGameMode());
        server.getEventManager().fire(event);

        if (level != event.getSpawnLevel()) {
            level.getEntityManager().deregisterEntity(this);
            NukkitLevel newLevel = (NukkitLevel) event.getSpawnLevel();
            newLevel.getEntityManager().registerEntity(this);
            setEntityId(newLevel.getEntityManager().allocateEntityId());
        }

        setPosition(event.getSpawnPosition());
        setRotation(event.getRotation());
        hasMoved = false;

        StartGamePacket startGame = new StartGamePacket();
        startGame.setUniqueEntityId(getEntityId());
        startGame.setRuntimeEntityId(getEntityId());
        startGame.setGamemode(event.getGameMode());
        startGame.setPlayerPosition(event.getSpawnPosition());
        startGame.setRotation(event.getRotation());
        startGame.setLevelSettings(event.getSpawnLevel().getData());
        startGame.setLevelId(event.getSpawnLevel().getId());
        startGame.setWorldName("world"); //level.getName()
        startGame.setPremiumWorldTemplateId("");
        startGame.setTrial(false);
        startGame.setCurrentTick(event.getSpawnLevel().getCurrentTick());
        startGame.setEnchantmentSeed(enchantmentSeed);
        session.addToSendQueue(startGame);

        sendCommandsEnabled();

        sendAttributes();
        sendAdventureSettings();
        sendPlayerInventory();
    }

    @Override
    public boolean isOnGround() {
        Vector3f position = getPosition();
        int chunkX = position.getFloorX() >> 4;
        int chunkY = position.getFloorY() >> 4;
        getLevel().getChunk(chunkX, chunkY).join();// Make sure the chunk is loaded before checking
        return super.isOnGround();
    }

    @Override
    public boolean isSpawned() {
        return spawned;
    }

    private void sendAttributes() {
        Damageable damageable = ensureAndGet(Damageable.class);
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        Attribute health = new Attribute("minecraft:health", damageable.getFixedHealth(), 0f,
                damageable.getMaximumHealth());

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getAttributes().add(health);
        packet.getAttributes().addAll(data.getAttributes());
        session.addToSendQueue(packet);
    }

    private void sendCreativeInventory() {

    }

    public void sendPlayerInventory() {

    }

    private void sendMovePlayer() {
        MovePlayerPacket packet = new MovePlayerPacket();

        packet.setRuntimeEntityId(getEntityId());
        packet.setPosition(getGamePosition());
        packet.setRotation(getRotation());
        packet.setOnGround(isOnGround());
        packet.setRidingRuntimeEntityId(0);
        if (isTeleported()) {
            packet.setMode(MovePlayerPacket.Mode.TELEPORT);
            packet.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
            packet.setUnknown0(1);
        } else {
            packet.setMode(MovePlayerPacket.Mode.NORMAL);
        }
        session.addToSendQueue(packet);
    }

    private void sendEntityData() {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getMetadata().putAll(getMetadataFlags());
        session.addToSendQueue(packet);
    }

    private void sendHealth() {
        Damageable damageable = ensureAndGet(Damageable.class);
        SetHealthPacket packet = new SetHealthPacket();
        packet.setHealth(Math.round(damageable.getFixedHealth()));
        session.addToSendQueue(packet);
    }

    private void sendGamerules() {
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        packet.setGameRules(level.getData().getGameRules());
        session.addToSendQueue(packet);
    }

    private void sendAdventureSettings() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        NukkitAbilities abilities = data.getAbilities();

        AdventureSettingsPacket packet = new AdventureSettingsPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setFlags(abilities.getFlags());
        packet.setFlags2(abilities.getFlags2());
        packet.setCustomFlags(abilities.getCustomFlags());
        packet.setCommandPermission(data.getCommandPermission());
        packet.setPlayerPermission(data.getPlayerPermission());
        session.addToSendQueue(packet);
    }

    private void sendCommandsEnabled() {
        SetCommandsEnabledPacket packet = new SetCommandsEnabledPacket();
        packet.setCommandsEnabled(this.commandsEnabled);
        session.addToSendQueue(packet);
    }

    private OptionalInt testInventoryChange(Inventory inventory) {
        byte windowId;
        if (inventory == openInventory) {
            windowId = NukkitInventoryType.fromApi(openInventory.getInventoryType()).getId();
        } else if (inventory == this.inventory) {
            windowId = NukkitInventoryType.PLAYER.getId();
        } else {
            return OptionalInt.empty(); // Player shouldn't be observing this inventory.
        }
        return OptionalInt.of(windowId);
    }

    @Nonnull
    @Override
    public String getName() {
        return session.getAuthData().getDisplayName();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return session.getAuthData().getIdentity();
    }

    @Nonnull
    @Override
    public Optional<String> getXuid() {
        return Optional.ofNullable(session.getAuthData().getXuid());
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean value) {
    }

    @Override
    public boolean isWhitelisted() {
        return server.getWhitelist().isWhitelisted(this);
    }

    @Override
    public void setWhitelisted(boolean value) {
        server.getWhitelist().whitelist(this);
    }

    public Optional<UUID> getOfflineUniqueId() {
        return Optional.ofNullable(session.getAuthData().getOfflineIdentity());
    }

    @Override
    public MinecraftPacket createAddEntityPacket() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        AddPlayerPacket addPlayer = new AddPlayerPacket();
        addPlayer.setUuid(session.getAuthData().getIdentity());
        addPlayer.setUsername(session.getAuthData().getDisplayName());
        addPlayer.setUniqueEntityId(getEntityId());
        addPlayer.setRuntimeEntityId(getEntityId());
        addPlayer.setPosition(getPosition());
        addPlayer.setMotion(getMotion());
        addPlayer.setRotation(getRotation());
        addPlayer.setHand(inventory.getItemInHand().orElse(null));
        addPlayer.getMetadata().putAll(getMetadata());
        addPlayer.setFlags(data.getAbilities().getFlags());
        addPlayer.setFlags2(data.getAbilities().getFlags2());
        addPlayer.setCommandPermission(data.getCommandPermission());
        addPlayer.setCustomFlags(data.getAbilities().getCustomFlags());
        addPlayer.setPlayerPermission(data.getPlayerPermission());
        // TODO: Adventure settings
        return addPlayer;
    }

    @Override
    public boolean transfer(@Nonnull InetSocketAddress address) {
        Preconditions.checkNotNull(address, "address");
        return transfer(address.getAddress().getHostAddress(), address.getPort());
    }

    @Override
    public boolean transfer(@Nonnull String address, @Nonnegative int port) {
        Preconditions.checkNotNull(address, "address");
        Preconditions.checkArgument(port <= 0xffff, "Port must be between 0 - 65535");
        PlayerTransferEvent event = new PlayerTransferEvent(this, address, port);
        server.getEventManager().fire(event);
        if (event.isCancelled()) {
            return false;
        }
        TransferPacket packet = new TransferPacket();
        packet.setAddress(address);
        packet.setPort(port);
        session.addToSendQueue(packet);
        return true;
    }

    @Override
    public boolean isBreakingBlock() {
        return false;
    }

    @Override
    public void showXboxProfile(String xuid) {
        Preconditions.checkState(Strings.isNullOrEmpty(xuid), "xuid is null or empty");
        ShowProfilePacket packet = new ShowProfilePacket();
        packet.setXuid(xuid);
        session.addToSendQueue(packet);
    }

    @Override
    public void showXboxProfile(Player player) {
        player.getXuid().ifPresent(this::showXboxProfile);
    }

    @Override
    public boolean getRemoveFormat() {
        return false;
    }

    @Override
    public boolean setRemoveFormat(boolean value) {
        return false;
    }

    @Override
    public boolean hasCommandsEnabled() {
        return commandsEnabled;
    }

    @Override
    public void setCommandsEnabled(boolean commandsEnabled) {
        if (this.commandsEnabled != commandsEnabled) {
            this.commandsEnabled = commandsEnabled;
            sendCommandsEnabled();
        }
    }

    @Override
    public Skin getSkin() {
        return ensureAndGet(PlayerData.class).getSkin();
    }

    @Override
    public void setSkin(Skin skin) {
        PlayerData data = ensureAndGet(PlayerData.class);
        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.setSkin(skin);
        packet.setOldSkinName(data.getSkin().getSkinId());
        packet.setNewSkinName(skin.getSkinId());
        packet.setUuid(getUniqueId());

        //TODO: Update player list

        ensureAndGet(PlayerData.class).setSkin(skin);
    }

    @Override
    public void setButtonText(String text) {

    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sleepOn(Vector3i pos) {

    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    @Override
    public int getSleepTicks() {
        return 0;
    }

    @Override
    public void stopSleep() {

    }

    @Override
    public Entity getEntityPlayerLookingAt() {
        return null;
    }

    @Override
    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @Override
    public int getViewDistance() {
        return viewDistance;
    }

    @Override
    public void sendActionBar(String message) {

    }

    @Override
    public void sendActionBar(String message, int fadein, int duration, int fadeout) {

    }

    @Override
    public boolean dropItem(ItemInstance item) {
        return false;
    }

    @Nonnull
    @Override
    public Optional<String> getDisplayName() {
        return Optional.empty();
    }

    @Override
    public void setDisplayName(@Nullable String name) {

    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Nonnull
    @Override
    public Optional<UUID> getOfflineUuid() {
        return Optional.ofNullable(session.getAuthData().getOfflineIdentity());
    }

    @Override
    public boolean isXboxAuthenticated() {
        return session.getAuthData().getXuid() != null;
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return session.getClientData().getDeviceOs();
    }

    @Override
    public boolean isEducationEdition() {
        return session.getClientData().isEducationMode();
    }

    @Nonnull
    @Override
    public String getDeviceModel() {
        return session.getClientData().getDeviceModel();
    }

    @Nonnull
    @Override
    public String getGameVersion() {
        return session.getClientData().getGameVersion();
    }

    @Nonnull
    @Override
    public String getServerAddress() {
        return session.getClientData().getServerAddress();
    }

    @Nonnull
    @Override
    public Optional<String> getActiveDirectoryRole() {
        return Optional.ofNullable(session.getClientData().getActiveDirectoryRole());
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean sprinting) {

    }

    @Override
    public int getExperienceLevel() {
        return 0;
    }

    @Override
    public void disconnect() {
        session.disconnect();
    }

    @Override
    public void disconnect(String reason) {
        session.disconnect(reason);
    }

    @Override
    public void chat(String message) {

    }

    @Override
    public void executeCommand(String command) {
        if (!commandsEnabled) {
            return;
        }
        command = command.trim();
        if (command.isEmpty() || command.contains("\0")) {
            sendMessage(new TranslationMessage("commands.generic.unknown"));
            return;
        }

        try {
            server.getCommandManager().executeCommand(PlayerSession.this, command);
        } catch (CommandNotFoundException e) {
            sendMessage(new TranslationMessage("commands.generic.unknown", command));
        } catch (CommandException e) {
            sendMessage(new TranslationMessage("commands.generic.exception"));
            throw new RuntimeException("An error occurred whilst running command " + command + " for " + getName(), e);
        }
    }

    @Override
    public void hideEntity(@Nonnull Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        hiddenEntities.add(entity.getEntityId());
        updateViewableEntities();
    }

    @Override
    public void showEntity(@Nonnull Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        hiddenEntities.remove(entity.getEntityId());
        updateViewableEntities();
    }

    @Override
    public boolean canSee(@Nonnull Entity entity) {
        Preconditions.checkNotNull(entity, "entity");
        return viewableEntities.contains(entity.getEntityId());
    }

    @Override
    public void sendTitle(String title, String subtitle) {

    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void resetTitle() {

    }

    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public boolean onItemPickup(ItemInstance item) {
        return inventory.addItem(item);
    }

    public NetworkPacketHandler getNetworkPacketHandler() {
        return new PlayerNetworkPacketHandler();
    }

    @Override
    public void onInventorySlotChange(int slot, @Nullable ItemInstance oldItem, @Nullable ItemInstance newItem, @Nonnull Inventory inventory, @Nullable PlayerSession session) {
        log.debug("Adding item to inventory");
        OptionalInt optionalId = testInventoryChange(inventory);
        if (!optionalId.isPresent()) {
            return;
        }
        byte windowId = (byte) optionalId.getAsInt();

        // The session that sent the inventory change updates without the need of a packet.
        if (session != this) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setSlot(newItem);
            packet.setInventorySlot(slot);
            packet.setWindowId(windowId);
            this.session.addToSendQueue(packet);
        }
    }

    @Override
    public void onInventoryContentsChange(@Nonnull ItemInstance[] contents, @Nonnull Inventory inventory) {
        OptionalInt optionalId = testInventoryChange(inventory);
        if (!optionalId.isPresent()) {
            return;
        }
        byte windowId = (byte) optionalId.getAsInt();

        InventoryContentPacket packet = new InventoryContentPacket();
        packet.setWindowId(windowId);
        packet.setItems(contents);
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        sendMessage(new RawMessage(text));
    }

    @Override
    public void sendMessage(@Nonnull Message text) {
        TextPacket packet = new TextPacket();
        packet.setMessage(text);
        packet.setXuid(getXuid().orElse(""));
        session.addToSendQueue(packet);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String name) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {

    }

    @Override
    public void setPosition(@Nonnull Vector3f position) {
        super.setPosition(position);
        hasMoved = true;
    }

    @Override
    public void setRotation(@Nonnull Rotation rotation) {
        super.setRotation(rotation);
        hasMoved = true;
    }

    @Override
    public void onAttributeUpdate(Attribute attribute) {
        Preconditions.checkNotNull(attribute, "attribute");
        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.getAttributes().add(attribute);
        session.addToSendQueue(packet);
    }

    @Override
    protected void onMetadataUpdate(MetadataDictionary metadata) {
        if (!spawned) {
            return;
        }
        super.onMetadataUpdate(metadata);
    }

    @Override
    @Deprecated
    public void remove() {
        session.disconnect();
    }

    void removeInternal() {
        super.remove();
    }

    public class PlayerNetworkPacketHandler implements NetworkPacketHandler {

        @Override
        public void handle(AdventureSettingsPacket packet) {
            PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);

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
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }

            PlayerAnimationEvent event = new PlayerAnimationEvent(PlayerSession.this, packet.getAction());
            server.getEventManager().fire(event);
            if (event.isCancelled()) {
                return;
            }

            level.getPacketManager().queuePacketForViewers(PlayerSession.this, packet);
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
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }

            PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(PlayerSession.this, packet.getCommand());
            server.getEventManager().fire(event);
            if (event.isCancelled()) {
                return;
            }

            executeCommand(packet.getCommand().substring(1));
        }

        @Override
        public void handle(ContainerClosePacket packet) {
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }

            if (openInventory != null) {
                ((NukkitInventory) openInventory).getObservers().remove(PlayerSession.this);
                openInventory = null;
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
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }

            Optional<BaseEntity> entity = level.getEntityManager().getEntityById(packet.getRuntimeEntityId());
            if (!entity.isPresent()) {
                return;
            }

            switch (packet.getAction()) {
                default:
                    if (log.isDebugEnabled()) {
                        log.debug("Unhandled InteractPacket received from {} with action: {}", getName(), packet.getAction().name());
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
            packet.getTransaction().execute(PlayerSession.this);
        }

        @Override
        public void handle(ItemFrameDropItemPacket packet) {

        }

        @Override
        public void handle(LevelSoundEventPacket packet) {
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }
            level.getPacketManager().queuePacketForViewers(PlayerSession.this, packet);
        }

        @Override
        public void handle(MapInfoRequestPacket packet) {

        }

        @Override
        public void handle(MobArmorEquipmentPacket packet) {

        }

        @Override
        public void handle(MobEquipmentPacket packet) {
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }
            int hotbarSlot = packet.getHotbarSlot();
            if (hotbarSlot < 0 || hotbarSlot > 8) {
                log.debug("{} sent hotbar slot {}. Expected 0-8", getName(), hotbarSlot);
                return;
            }

            int inventorySlot = packet.getInventorySlot();
            int slot = inventorySlot < 0 || inventorySlot >= inventory.getInventoryType().getSize() ? -1 : inventorySlot;

            ItemInstance serverItem = inventory.getItem(slot).orElse(BlockUtil.AIR);
            if (!serverItem.equals(packet.getItem())) {
                log.debug("{} tried to equip {} but has {} in slot {}", getName(), packet.getItem(), serverItem, hotbarSlot);
                sendPlayerInventory();
                return;
            }

            inventory.setHotbarLink(hotbarSlot, slot);
            inventory.setHeldHotbarSlot(hotbarSlot, false);
        }

        @Override
        public void handle(ModalFormResponsePacket packet) {

        }

        @Override
        public void handle(MoveEntityPacket packet) {

        }

        @Override
        public void handle(MovePlayerPacket packet) {
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }

            Vector3f oldPosition = getPosition();
            Vector3f newPosition = packet.getPosition().sub(0, getOffset(), 0);

            if (newPosition.distanceSquared(newPosition) >= 10000) {
                setPosition(oldPosition);
                setRotation(packet.getRotation());
                return;
            }

            setPosition(newPosition);
            setRotation(packet.getRotation());
            // If we haven't moved in the X or Z axis, don't update viewable entities or try updating chunks - they haven't changed.
            if (hasSubstantiallyMoved(oldPosition, newPosition)) {
                updateViewableEntities();
                sendNewChunks().exceptionally(throwable -> {
                    log.error("Unable to send chunks", throwable);
                    disconnect("disconnect.disconnected");
                    return null;
                });
            }
        }

        @Override
        public void handle(PhotoTransferPacket packet) {

        }

        @Override
        public void handle(PlayerActionPacket packet) {
            Damageable damageable = ensureAndGet(Damageable.class);
            if (!spawned || damageable.isDead()) {
                return;
            }
            PlayerData data = ensureAndGet(PlayerData.class);

            switch (packet.getAction()) {
                case START_BREAK:
                    if (data.getGameMode() != GameMode.SURVIVAL) {
                        return;
                    }
                    Optional<Block> block = getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                    if (!block.isPresent()) {
                        log.debug("{} attempted to break an unloaded block at {}", getName(), packet.getBlockPosition());
                        return;
                    }

                    ItemInstance inHand = inventory.getItemInHand().orElse(BlockUtil.AIR);

                    BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.get().getBlockState().getBlockType());
                    float breakTime = blockBehavior.getBreakTime(PlayerSession.this, block.get(), inHand);
                    getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_START_BREAK, (int) (65535 / breakTime));
                    return;
                case CONTINUE_BREAK:
                    if (data.getGameMode() != GameMode.SURVIVAL) {
                        return;
                    }
                    Optional<Block> blockBreakingOptional = getLevel().getBlockIfChunkLoaded(packet.getBlockPosition());
                    if (!blockBreakingOptional.isPresent()) {
                        log.debug("{} attempted to break an unloaded block at {}", getName(), packet.getBlockPosition());
                        return;
                    }
                    BlockState blockBreakingState = blockBreakingOptional.get().getBlockState();
                    if (!blockBreakingState.getBlockType().isDiggable()) {
                        return;
                    }
                    int blockData = NukkitLevel.getPaletteManager().getOrCreateRuntimeId(blockBreakingState) | (packet.getFace().ordinal() << 24);
                    getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.PARTICLE_PUNCH_BLOCK, blockData);
                    return;
                case ABORT_BREAK:
                case STOP_BREAK:
                    getLevel().getPacketManager().queueEventForViewers(packet.getBlockPosition().toFloat(), LevelEventPacket.Event.BLOCK_STOP_BREAK, 0);
                    return;
                case GET_UPDATED_BLOCK:
                case DROP_ITEM:
                case START_SLEEP:
                case STOP_SLEEP:
                case RESPAWN:
                    sendHealth();
                case JUMP:
                case START_SPRINT:
                    data.setSprinting(true);
                    break;
                case STOP_SPRINT:
                    data.setSprinting(false);
                    break;
                case START_SNEAK:
                    setFlag(MetadataConstants.Flag.SNEAKING, true);
                case STOP_SNEAK:
                    setFlag(MetadataConstants.Flag.SNEAKING, false);
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
            session.sendImmediatePackage(radiusPacket);
            viewDistance = radius;

            sendNewChunks().whenComplete((chunks, throwable) -> {
                if (throwable != null) {
                    log.error("Unable to load chunks", throwable);
                    session.disconnect();
                    return;
                }
                // If the player has not spawned, we need to start the spawning sequence.
                if (!spawned) {
                    sendEntityData();

                    PlayStatusPacket playStatus = new PlayStatusPacket();
                    playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
                    session.sendImmediatePackage(playStatus);

                    SetTimePacket setTime = new SetTimePacket();
                    setTime.setTime(level.getTime());
                    session.sendImmediatePackage(setTime);

                    updateViewableEntities();
                    sendMovePlayer();

                    //session.addToSendQueue(server.getCommandManager().createAvailableCommandsPacket(PlayerSession.this));

                    spawned = true;

                    TranslationMessage joinMessage = new TranslationMessage("multiplayer.player.joined", getName());
                    log.info(TranslatableMessage.of(joinMessage));

                    PlayerJoinEvent event = new PlayerJoinEvent(PlayerSession.this, joinMessage);
                    server.getEventManager().fire(event);

                    event.getJoinMessage().ifPresent(PlayerSession.this::sendMessage);
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
            session.addToSendQueue(chunkData);
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
                        startGame();
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
                    session.addToSendQueue(stackPacket);
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
                        session.addToSendQueue(dataInfoPacket);
                    }
                    return;
                case REFUSED:
                    if (forcePacks) {
                        session.disconnect("disconnectionScreen.resourcePack");
                        return;
                    }
                    // Fall through
                case COMPLETED:
                    startGame();
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
            Damageable health = ensureAndGet(Damageable.class);
            if (!spawned || health.isDead()) {
                return;
            }

            if (!(packet.getMessage() instanceof ChatMessage)) {
                if (log.isDebugEnabled()) {
                    log.debug("{} sent {} when only ChatMessages are allowed", getName(), packet.getMessage().getClass().getName());
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
                executeCommand(command);
            }

            PlayerChatEvent event = new PlayerChatEvent(PlayerSession.this, message);
            if (event.isCancelled()) {
                return;
            }

            packet.setXuid(""); // Stop chat issues
            packet.setMessage(new ChatMessage(getName(), messageString, false)); // To stop any name forgery.
            session.addToSendQueue(packet);
        }

        // Transactions (Not packets but here for convenience)

        public void handle(NormalTransaction transaction) {

        }

        public void handle(InventoryMismatchTransaction transaction) {

        }

        public void handle(ItemUseTransaction transaction) {

        }

        public void handle(ItemUseOnEntityTransaction transaction) {

        }

        public void handle(ItemReleaseTransaction transaction) {

        }
    }

    public static final System SYSTEM = System.builder()
            .expectComponent(PlayerData.class)
            .runner(new PlayerTickSystem())
            .build();

    private static class PlayerTickSystem implements SystemRunner {

        @Override
        public void run(Entity entity) {
            Preconditions.checkArgument(entity instanceof PlayerSession, "Entity was not of type PlayerSession");
            PlayerSession session = (PlayerSession) entity;

            DamageableComponent damageable = (DamageableComponent) session.ensureAndGet(Damageable.class);
            if (!session.spawned || session.getMinecraftSession().isClosed() || damageable.isDead()) {
                return;
            }

            PlayerDataComponent playerData = (PlayerDataComponent) session.ensureAndGet(PlayerData.class);

            if (session.hasMoved) {
                session.hasMoved = false;
                if (session.isTeleported()) {
                    session.sendMovePlayer();
                }
                session.updateViewableEntities();
                session.sendNewChunks().exceptionally(throwable -> {
                    log.error("Unable to send chunks", throwable);
                    session.disconnect("Internal server error");
                    return null;
                });
            }


        }
    }

    private static long getChunkKey(int x, int z) {
        return ((long) x << 32) + z - Integer.MIN_VALUE;
    }

    private static boolean hasSubstantiallyMoved(Vector3f oldPos, Vector3f newPos) {
        return (Float.compare(oldPos.getX(), newPos.getX()) != 0 || Float.compare(oldPos.getZ(), newPos.getZ()) != 0);
    }

    public String toString() {
        return "PlayerSession(" +
                "name='" + getName() +
                "', address=" + getRemoteAddress().map(InetSocketAddress::toString).orElse("UNKNOWN") +
                ')';
    }
}
