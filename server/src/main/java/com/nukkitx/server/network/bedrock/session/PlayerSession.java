package com.nukkitx.server.network.bedrock.session;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nukkitx.api.Player;
import com.nukkitx.api.command.CommandException;
import com.nukkitx.api.command.CommandNotFoundException;
import com.nukkitx.api.container.Container;
import com.nukkitx.api.container.FillingContainer;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;
import com.nukkitx.api.event.player.PlayerLoginEvent;
import com.nukkitx.api.event.player.PlayerTransferEvent;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.message.*;
import com.nukkitx.api.permission.Permission;
import com.nukkitx.api.permission.PermissionAttachment;
import com.nukkitx.api.permission.PermissionAttachmentInfo;
import com.nukkitx.api.plugin.PluginContainer;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.api.util.Skin;
import com.nukkitx.api.util.data.DeviceOS;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.container.ContainerContentListener;
import com.nukkitx.server.container.ContainerType;
import com.nukkitx.server.container.NukkitFillingContainer;
import com.nukkitx.server.container.manager.ContainerManager;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.entity.component.DamageableComponent;
import com.nukkitx.server.entity.component.PlayerDataComponent;
import com.nukkitx.server.inventory.Inventories;
import com.nukkitx.server.inventory.NukkitPlayerInventory;
import com.nukkitx.server.inventory.transaction.InventoryTransactionManager;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.chunk.FullChunkDataPacketCreator;
import com.nukkitx.server.level.util.AroundPointChunkComparator;
import com.nukkitx.server.network.bedrock.session.data.AuthData;
import com.nukkitx.server.network.bedrock.session.data.ClientData;
import com.nukkitx.server.permission.NukkitAbilities;
import com.spotify.futures.CompletableFutures;
import gnu.trove.TCollections;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PlayerSession extends LivingEntity implements Player, ContainerContentListener {
    public static final System SYSTEM = System.builder()
            .expectComponent(PlayerData.class)
            .runner(new PlayerTickSystem())
            .build();
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(PlayerSession.class);
    private final BedrockServerSession session;
    private final NukkitServer server;
    @Getter
    private final AuthData authData;
    @Getter
    private final ClientData clientData;
    private final TLongSet viewableEntities = new TLongHashSet();
    private final TLongSet hiddenEntities = TCollections.synchronizedSet(new TLongHashSet());
    private final TLongSet sentChunks = TCollections.synchronizedSet(new TLongHashSet());
    private final AtomicReference<Locale> locale = new AtomicReference<>();
    private final Set<UUID> playersListed = new CopyOnWriteArraySet<>();
    private final NukkitPlayerInventory inventory = new NukkitPlayerInventory(this);
    @Getter
    private final PlayerSessionPacketDispatcher dispatcher = new PlayerSessionPacketDispatcher(this);
    private final AtomicInteger containerCounter = new AtomicInteger(0);
    private boolean commandsEnabled = true;
    private boolean hasMoved;
    private final AtomicReference<Container> openContainer = new AtomicReference<>(null);
    @Getter
    private final InventoryTransactionManager transactionManager = new InventoryTransactionManager(this);
    private final AtomicReference<ContainerManager> containerManager = new AtomicReference<>(null);
    private FillingContainer untrackedInteractionContainer = new NukkitFillingContainer(this,
            Inventories.UNTRACKED_INTERACTION_SLOT_COUNT, ContainerType.UNTRACKED_UI_INTERACTION);
    @Getter
    private ContainerId selectedContainerId = null;
    private ItemStack cursorSelectedItem = null;
    @Getter
    private int enchantmentSeed;
    private int viewDistance;
    private boolean spawned = false;

    public PlayerSession(LoginSession session, NukkitLevel level) {
        super(EntityType.PLAYER, level.getData().getDefaultSpawn(), level, level.getServer(), 20);
        this.session = session.getBedrockSession();
        this.server = level.getServer();
        this.authData = session.getAuthData();
        this.clientData = session.getClientData();
        this.session.addDisconnectHandler(new PlayerDisconnectHandler());
        Path playerDatPath = server.getPlayersPath().resolve(getXuid().isPresent() ? getUniqueId().toString() : getName());
        locale.set(server.getLocaleManager().getLocaleByString(session.getClientData().getLanguageCode()));

        enchantmentSeed = ThreadLocalRandom.current().nextInt();
        viewDistance = server.getConfiguration().getMechanics().getViewDistance();

        // Register components
        registerComponent(PlayerData.class, new PlayerDataComponent(this));
    }

    private static long getChunkKey(int x, int z) {
        return ((long) x << 32) + z - Integer.MIN_VALUE;
    }

    static boolean hasSubstantiallyMoved(Vector3f oldPos, Vector3f newPos) {
        return (Float.compare(oldPos.getX(), newPos.getX()) != 0 || Float.compare(oldPos.getZ(), newPos.getZ()) != 0);
    }

    public BedrockServerSession getBedrockSession() {
        return session;
    }

    public boolean isChunkInView(int x, int z) {
        return sentChunks.contains(getChunkKey(x, z));
    }

    public void sendNetworkPacket(BedrockPacket packet) {
        session.sendPacket(packet);
    }

    public Optional<ItemStack> getCursorSelectedItem() {
        return Optional.ofNullable(cursorSelectedItem);
    }

    public void setCursorSelectedItem(@Nullable ItemStack item) {
        if (!Objects.equals(item, cursorSelectedItem)) {
            InventorySource source = InventorySource.fromContainerWindowId(ContainerId.CURSOR);
            ItemData fromItem = ItemUtils.toNetwork(cursorSelectedItem);
            ItemData toItem = ItemUtils.toNetwork(item);
            InventoryAction action = new InventoryAction(source, 0, fromItem, toItem);
            // TODO: 20/12/2018 add action to inventory transaction manager
        }
    }

    public CompletableFuture<List<Chunk>> sendNewChunks() {
        return getChunksForRadius(viewDistance).whenComplete(((chunks, throwable) -> {
            if (throwable != null) {
                log.error("Cannot load chunks for" + this.authData.getDisplayName());
                return;
            }

            // Send closest chunks first.
            Vector3f position = getPosition();
            int chunkX = position.getFloorX() >> 4;
            int chunkZ = position.getFloorZ() >> 4;

            chunks.sort(new AroundPointChunkComparator(chunkX, chunkZ));

            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.setRadius(viewDistance << 4);
            packet.setPosition(getPosition().toInt());
            if (chunks.size() > 0) {
                this.sendNetworkPacket(packet);
            }

            for (Chunk chunk : chunks) {
                this.sendNetworkPacket(((FullChunkDataPacketCreator) chunk).createFullChunkDataPacket());
            }
        }));
    }

    public void updateViewableEntities() {
        synchronized (viewableEntities) {
            Collection<BaseEntity> inView = this.getLevel().getEntityManager().getEntitiesInDistance(getPosition(), server.getConfiguration().getMechanics().getViewDistance() << 4);
            TLongSet toRemove = new TLongHashSet();
            Collection<BaseEntity> toAdd = new ArrayList<>();

            // Remove entities out of range
            viewableEntities.forEach(id -> {
                Optional<BaseEntity> optionalEntity = this.getLevel().getEntityManager().getEntityById(id);
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
                if (entity == this || (entity instanceof PlayerSession &&
                        (!playersListed.contains(((PlayerSession) entity).getUniqueId())))) {
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
                sendNetworkPacket(packet);
                return true;
            });

            for (BaseEntity entity : toAdd) {
                sendNetworkPacket(entity.createAddEntityPacket());
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

    void startGame() {
        PlayerLoginEvent event = new PlayerLoginEvent(this, this.getLevel().getData().getDefaultSpawn(), this.getLevel(), getRotation(), this.getLevel().getData().getGameMode());
        server.getEventManager().fire(event);

        if (this.getLevel() != event.getSpawnLevel()) {
            this.getLevel().getEntityManager().deregisterEntity(this);
            NukkitLevel newLevel = (NukkitLevel) event.getSpawnLevel();
            newLevel.getEntityManager().registerEntity(this);
            setEntityId(newLevel.getEntityManager().allocateEntityId());
        }

        setPosition(event.getSpawnPosition());
        setRotation(event.getRotation());
        hasMoved = false;

        dispatcher.sendEmptyInventory();
        dispatcher.sendStartGame();
        updatePlayerList();
        sendEntityData();
        sendAdventureSettings();
        updateViewableEntities();

        dispatcher.sendCommandsEnabled();

        sendAttributes();
        sendAdventureSettings();
        dispatcher.sendInventory();
    }

    public boolean setUntrackedInterationItem(int slot, ItemStack item) {
        ItemStack slotItem = untrackedInteractionContainer.getSlot(slot);
        if (slotItem.equals(item)) {
            return false;
        } else {
            untrackedInteractionContainer.setSlot(slot, item);
            return true;
        }
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

    @Override
    public Optional<Container> getOpenContainer() {
        return Optional.ofNullable(openContainer.get());
    }

    public Optional<ContainerManager> getContainerManager() {
        return Optional.ofNullable(containerManager.get());
    }

    void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }


    private void nextContainerCounter() {
        containerCounter.incrementAndGet();
        containerCounter.compareAndSet(101, 1);
    }

    public boolean openAnvil(Vector3i blockPosition) {
        if (canOpenContainer()) {

            ContainerOpenPacket containerOpen = new ContainerOpenPacket();
            containerOpen.setWindowId(containerCounter.byteValue());
            containerOpen.setUniqueEntityId(-1);
            containerOpen.setType((byte) 5);
            // TODO: 09/01/2019 Finish container opening
        }
        return false;
    }


    public boolean canOpenContainer() {
        return containerManager.get() != null; // TODO: 21/12/2018 check if inside a portal
    }

    public void updatePlayerList() {
        Set<Player> toAdd = new HashSet<>();
        Set<UUID> toRemove = new HashSet<>();
        Map<UUID, PlayerSession> availableSessions = new HashMap<>();

        for (PlayerSession session : getLevel().getEntityManager().getPlayers()) {
            availableSessions.put(session.getUniqueId(), session);
        }

        for (PlayerSession session : availableSessions.values()) {
            if (playersListed.add(session.getUniqueId())) {
                toAdd.add(session);
            }
        }

        for (UUID uuid : playersListed) {
            if (!availableSessions.containsKey(uuid)) {
                toRemove.add(uuid);
            }
        }

        if (!toAdd.isEmpty()) {
            PlayerListPacket list = new PlayerListPacket();
            list.setType(PlayerListPacket.Type.ADD);
            for (Player player : toAdd) {
                PlayerData data = player.ensureAndGet(PlayerData.class);
                PlayerListPacket.Entry entry = new PlayerListPacket.Entry(player.getUniqueId());
                entry.setEntityId(player.getEntityId());
                Skin skin = data.getSkin();
                entry.setSkinId(skin.getSkinId());
                entry.setSkinData(skin.getSkinData());
                entry.setCapeData(skin.getCapeData());
                entry.setGeometryName(skin.getGeometryName());
                entry.setGeometryData(skin.getGeometryData());
                entry.setName(player.getName());
                entry.setXuid(player.getXuid().orElse(""));
                entry.setPlatformChatId("");
                list.getEntries().add(entry);
            }
            sendNetworkPacket(list);
        }

        if (!toRemove.isEmpty()) {
            playersListed.removeAll(toRemove);

            PlayerListPacket list = new PlayerListPacket();
            list.setType(PlayerListPacket.Type.REMOVE);
            for (UUID uuid : toRemove) {
                list.getEntries().add(new PlayerListPacket.Entry(uuid));
            }
            sendNetworkPacket(list);
        }
    }

    private void sendAttributes() {
        Damageable damageable = ensureAndGet(Damageable.class);
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        Attribute health = new Attribute("minecraft:health", 0f,
                damageable.getMaximumHealth(), damageable.getFixedHealth(), 10f);

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getAttributes().add(health);
        packet.getAttributes().addAll(data.getAttributes());
        sendNetworkPacket(packet);
    }


    public boolean isTeleported() {
        return super.isTeleported();
    }

    void sendEntityData() {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getMetadata().putAll(getMetadata());
        sendNetworkPacket(packet);
    }

    void sendHealth() {
        Damageable damageable = ensureAndGet(Damageable.class);
        SetHealthPacket packet = new SetHealthPacket();
        packet.setHealth(Math.round(damageable.getFixedHealth()));
        sendNetworkPacket(packet);
    }

    void sendAdventureSettings() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        NukkitAbilities abilities = data.getAbilities();

        AdventureSettingsPacket packet = new AdventureSettingsPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setPlayerFlags(abilities.getFlags());
        packet.setWorldFlags(abilities.getFlags2());
        packet.setCustomFlags(abilities.getCustomFlags());
        packet.setCommandPermission(data.getCommandPermission().ordinal());
        packet.setPlayerPermission(data.getPlayerPermission().ordinal());
        sendNetworkPacket(packet);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.authData.getDisplayName();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return this.authData.getIdentity();
    }

    @Nonnull
    @Override
    public Optional<String> getXuid() {
        return Optional.ofNullable(this.authData.getXuid());
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

    @Override
    public BedrockPacket createAddEntityPacket() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        AddPlayerPacket addPlayer = new AddPlayerPacket();
        addPlayer.setUuid(this.authData.getIdentity());
        addPlayer.setUsername(this.authData.getDisplayName());
        addPlayer.setUniqueEntityId(getEntityId());
        addPlayer.setRuntimeEntityId(getEntityId());
        addPlayer.setPlatformChatId("");
        addPlayer.setPosition(getGamePosition());
        addPlayer.setMotion(getMotion());
        addPlayer.setRotation(getRotation().toVector3f());
        addPlayer.setHand(ItemUtils.toNetwork(inventory.getSelectedItem()));
        addPlayer.getMetadata().putAll(getMetadata());
        addPlayer.setPlayerFlags(data.getAbilities().getFlags());
        addPlayer.setCommandPermission(data.getCommandPermission().ordinal());
        addPlayer.setWorldFlags(data.getAbilities().getFlags2());
        addPlayer.setPlayerPermission(data.getPlayerPermission().ordinal());
        addPlayer.setCustomFlags(data.getAbilities().getCustomFlags());
        addPlayer.setDeviceId(this.clientData.getDeviceId());
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
        sendNetworkPacket(packet);
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
        sendNetworkPacket(packet);
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
            dispatcher.sendCommandsEnabled();
        }
    }

    @Override
    public Skin getSkin() {
        return ensureAndGet(PlayerData.class).getSkin();
    }

    @Override
    public void setSkin(Skin skin) {
        PlayerData data = ensureAndGet(PlayerData.class);

        data.setSkin(skin);

        PlayerSkinPacket playerSkin = new PlayerSkinPacket();
        playerSkin.setSkinId(skin.getSkinId());
        playerSkin.setSkinData(skin.getSkinData());
        playerSkin.setCapeData(skin.getCapeData());
        playerSkin.setGeometryName(skin.getGeometryName());
        playerSkin.setGeometryData(skin.getGeometryData());
        playerSkin.setOldSkinName(data.getSkin().getSkinId());
        playerSkin.setNewSkinName(skin.getSkinId());
        playerSkin.setUuid(getUniqueId());

        // TODO: 20/12/2018 Add cancellable event.
        this.getLevel().getPacketManager().queuePacketForViewers(this, playerSkin);
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
    public int getViewDistance() {
        return viewDistance;
    }

    @Override
    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @Override
    public void sendActionBar(String message) {

    }

    @Override
    public void sendActionBar(String message, int fadein, int duration, int fadeout) {

    }

    @Override
    public boolean drop(ItemStack item) {
        return drop(item, false);
    }

    public boolean drop(ItemStack item, boolean drop) {
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
        return Optional.of(session.getAddress());
    }

    @Override
    public boolean isXboxAuthenticated() {
        return this.authData.getXuid() != null;
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return this.clientData.getDeviceOs();
    }

    @Override
    public boolean isEducationEdition() {
        return this.clientData.isEduMode();
    }

    @Nonnull
    @Override
    public String getDeviceModel() {
        return this.clientData.getDeviceModel();
    }

    @Nonnull
    @Override
    public String getGameVersion() {
        return this.clientData.getGameVersion();
    }

    @Nonnull
    @Override
    public String getServerAddress() {
        return this.clientData.getServerAddress();
    }

    @Nonnull
    @Override
    public Optional<String> getActiveDirectoryRole() {
        return Optional.ofNullable(this.clientData.getActiveDirectoryRole());
    }

    @Override
    public int getExperienceLevel() {
        return 0;
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public void disconnect() {
        disconnect(null);
    }

    @Override
    public void disconnect(@Nullable String reason) {
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
    public boolean onItemPickup(ItemStack item) {
        return inventory.addItem(item);
    }

    BedrockPacketHandler getNetworkPacketHandler() {
        return new PlayerSessionPacketHandler(this);
    }

    @Override
    public void onSlotChange(int slot, @Nullable ItemStack oldItem, @Nullable ItemStack newItem, @Nonnull Container container, @Nullable PlayerSession session) {
        log.debug("Adding item to inventory");

        // The session that sent the inventory change updates without the need of a packet.
        if (session != this) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setSlot(ItemUtils.toNetwork(newItem));
            packet.setInventorySlot(slot);
            packet.setContainerId(containerManager.get().getContainerId());
            this.sendNetworkPacket(packet);
        }
    }

    @Override
    public void onContainerContentChange(@Nonnull ItemStack[] contents, @Nonnull Container container) {
        ContainerId id = containerManager.get().getContainerId();

        InventoryContentPacket packet = new InventoryContentPacket();
        packet.setContainerId(id);
        packet.setContents(ItemUtils.toNetwork(contents));

        sendNetworkPacket(packet);
    }

    @Override
    public void sendMessage(@Nonnull String message) {
        sendMessage(new RawMessage(message));
    }

    @Override
    public void sendMessage(@Nonnull Message message) {
        TextPacket packet = new TextPacket();
        packet.setMessage(message.getMessage());
        packet.setNeedsTranslation(message.needsTranslating());
        packet.setType(TextPacket.Type.valueOf(message.getType().name()));
        packet.setXuid(getXuid().orElse(""));
        if (message instanceof SourceMessage) {
            packet.setSourceName(((SourceMessage) message).getSender());
        }
        if (message instanceof ParameterMessage) {
            for (String parameter : ((ParameterMessage) message).getParameters()) {
                packet.getParameters().add(parameter);
            }
        }
        sendNetworkPacket(packet);
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
    public PermissionAttachment addAttachment(PluginContainer plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(PluginContainer plugin, String name) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(PluginContainer plugin, String name, Boolean value) {
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
        sendNetworkPacket(packet);
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
        throw new UnsupportedOperationException("Players cannot be removed");
    }

    private void removeInternal() {
        super.remove();
    }

    public NukkitPlayerInventory getInventory() {
        return this.inventory;
    }

    public void close() {
        this.session.disconnect();
    }

    public boolean isClosed() {
        return isRemoved();
    }

    public String toString() {
        return "NukkitPlayerSession(" +
                "name='" + getName() +
                "', address=" + getRemoteAddress().map(InetSocketAddress::toString).orElse("UNKNOWN") +
                ')';
    }

    private static class PlayerTickSystem implements SystemRunner {

        @Override
        public void run(Entity entity) {
            Preconditions.checkArgument(entity instanceof PlayerSession, "Entity was not of type NukkitPlayerSession");
            PlayerSession player = (PlayerSession) entity;

            DamageableComponent damageable = (DamageableComponent) player.ensureAndGet(Damageable.class);
            if (!player.spawned || player.session.isClosed() || damageable.isDead()) {
                return;
            }

            PlayerDataComponent playerData = (PlayerDataComponent) player.ensureAndGet(PlayerData.class);

            if (player.hasMoved) {
                player.hasMoved = false;
                if (player.isTeleported()) {
                    player.getDispatcher().sendMovePlayer();
                }
                player.updateViewableEntities();
                player.sendNewChunks().exceptionally(throwable -> {
                    log.error("Unable to send chunks", throwable);
                    player.disconnect("disconnect.endOfStream");
                    return null;
                });
            }

            player.updatePlayerList();
        }
    }

    private class PlayerDisconnectHandler implements Consumer<DisconnectReason> {

        @Override
        public void accept(DisconnectReason reason) {
            String reasonI18n = null;
            switch (reason) {
                case TIMED_OUT:
                    reasonI18n = "disconnect.timeout";
                    break;
                case DISCONNECTED:
                    break;
                case CLOSED_BY_REMOTE_PEER:
                    reasonI18n = "disconnect.closed";
                    break;
                default:
                    reasonI18n = "disconnect.disconnected";
                    break;
            }

            if (reasonI18n != null) {
                log.info("{} ({}) has been disconnected from the server: {}", PlayerSession.this.authData.getDisplayName(),
                        getRemoteAddress().map(Object::toString).orElse("UNKNOWN"), server.getLocaleManager().replaceI18n(reasonI18n));
                removeInternal();
            }
            PlayerSession.this.server.getSessionManager().remove(PlayerSession.this);
        }
    }
}
