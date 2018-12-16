package com.nukkitx.server.network.bedrock.session;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nukkitx.api.Player;
import com.nukkitx.api.command.CommandException;
import com.nukkitx.api.command.CommandNotFoundException;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;
import com.nukkitx.api.event.player.PlayerLoginEvent;
import com.nukkitx.api.event.player.PlayerTransferEvent;
import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.level.GameRules;
import com.nukkitx.api.level.LevelData;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.message.*;
import com.nukkitx.api.permission.Permission;
import com.nukkitx.api.permission.PermissionAttachment;
import com.nukkitx.api.permission.PermissionAttachmentInfo;
import com.nukkitx.api.plugin.PluginContainer;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.api.util.Skin;
import com.nukkitx.api.util.data.DeviceOS;
import com.nukkitx.protocol.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.MetadataDictionary;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.entity.component.DamageableComponent;
import com.nukkitx.server.entity.component.PlayerDataComponent;
import com.nukkitx.server.inventory.InventoryObserver;
import com.nukkitx.server.inventory.NukkitInventoryType;
import com.nukkitx.server.inventory.NukkitPlayerInventory;
import com.nukkitx.server.item.ItemUtil;
import com.nukkitx.server.level.NukkitGameRules;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.chunk.FullChunkDataPacketCreator;
import com.nukkitx.server.level.util.AroundPointChunkComparator;
import com.nukkitx.server.permission.NukkitAbilities;
import com.spotify.futures.CompletableFutures;
import gnu.trove.TCollections;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
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
import java.util.concurrent.atomic.AtomicReference;

public class NukkitPlayerSession extends LivingEntity implements PlayerSession, Player, InventoryObserver {
    public static final System SYSTEM = System.builder()
            .expectComponent(PlayerData.class)
            .runner(new PlayerTickSystem())
            .build();
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(NukkitPlayerSession.class);
    private final BedrockSession<NukkitPlayerSession> session;
    private final NukkitServer server;
    private final NukkitLevel level;
    private final TLongSet viewableEntities = new TLongHashSet();
    private final TLongSet hiddenEntities = TCollections.synchronizedSet(new TLongHashSet());
    private final TLongSet sentChunks = TCollections.synchronizedSet(new TLongHashSet());
    private final AtomicReference<Locale> locale = new AtomicReference<>();
    private final Set<UUID> playersListed = new CopyOnWriteArraySet<>();
    private final NukkitPlayerInventory inventory = new NukkitPlayerInventory(this);
    private boolean commandsEnabled = true;
    private boolean hasMoved;
    private Inventory openInventory;
    private int enchantmentSeed;
    private int viewDistance;
    private boolean spawned = false;

    public NukkitPlayerSession(BedrockSession<NukkitPlayerSession> session, NukkitLevel level) {
        super(EntityType.PLAYER, level.getData().getDefaultSpawn(), level, level.getServer(), 20);
        this.level = level;
        this.session = session;
        this.server = level.getServer();
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

    public BedrockSession<NukkitPlayerSession> getBedrockSession() {
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
                session.sendPacket(((FullChunkDataPacketCreator) chunk).createFullChunkDataPacket());
            }
        }));
    }

    public void updateViewableEntities() {
        synchronized (viewableEntities) {
            Collection<BaseEntity> inView = level.getEntityManager().getEntitiesInDistance(getPosition(), server.getConfiguration().getMechanics().getViewDistance() << 4);
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
                if (entity == this || (entity instanceof NukkitPlayerSession &&
                        (!playersListed.contains(((NukkitPlayerSession) entity).getUniqueId())))) {
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
                session.sendPacket(packet);
                return true;
            });

            for (BaseEntity entity : toAdd) {
                session.sendPacket(entity.createAddEntityPacket());
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
        startGame.setPlayerGamemode(event.getGameMode().ordinal());
        startGame.setPlayerPosition(event.getSpawnPosition());
        startGame.setRotation(event.getRotation().toVector2f());
        // Level Settings
        LevelData data = event.getSpawnLevel().getData();
        startGame.setSeed(data.getSeed());
        startGame.setDimensionId(data.getDimension().ordinal());
        startGame.setGeneratorId(data.getGenerator().ordinal());
        startGame.setLevelGamemode(data.getGameMode().ordinal());
        startGame.setDifficulty(data.getDifficulty().ordinal());
        startGame.setDefaultSpawn(data.getDefaultSpawn().toInt());
        startGame.setAcheivementsDisabled(data.isAchievementsDisabled());
        startGame.setTime(data.getTime());
        startGame.setEduLevel(data.isEduWorld());
        startGame.setEduFeaturesEnabled(data.isEduFeaturesEnabled());
        startGame.setRainLevel(data.getRainLevel());
        startGame.setLightningLevel(data.getLightningLevel());
        startGame.setMultiplayerGame(data.isMultiplayerGame());
        startGame.setBroadcastingToLan(data.isBroadcastingToLan());
        startGame.setBroadcastingToXbl(data.isBroadcastingToXBL());
        startGame.setCommandsEnabled(data.isCommandsEnabled());
        startGame.setTexturePacksRequired(data.isTexturepacksRequired());
        startGame.setBonusChestEnabled(data.isBonusChestEnabled());
        startGame.setStartingWithMap(data.isStartingWithMap());
        startGame.setTrustingPlayers(data.isTrustingPlayers());
        startGame.setDefaultPlayerPermission(data.getDefaultPlayerPermission().ordinal());
        startGame.setXblBroadcastMode(data.getXBLBroadcastMode());
        startGame.setServerChunkTickRange(data.getServerChunkTickRange());
        startGame.setBroadcastingToPlatform(data.isBroadcastingToPlatform());
        startGame.setPlatformBroadcastMode(data.getPlatformBroadcastMode());
        startGame.setIntentOnXblBroadcast(data.isIntentOnXBLBroadcast());
        startGame.setBehaviorPackLocked(data.isBehaviorPackLocked());
        startGame.setResourcePackLocked(data.isResourcePackLocked());
        startGame.setFromLockedWorldTemplate(data.isFromLockedWorldTemplate());
        startGame.setUsingMsaGamertagsOnly(data.isUsingMsaGamertagsOnly());
        startGame.setFromWorldTemplate(false);
        startGame.setWorldTemplateOptionLocked(false);

        startGame.setLevelId(event.getSpawnLevel().getId());
        startGame.setWorldName("world"); //level.getName()
        startGame.setPremiumWorldTemplateId("");
        startGame.setTrial(false);
        startGame.setCurrentTick(event.getSpawnLevel().getCurrentTick());
        startGame.setEnchantmentSeed(enchantmentSeed);
        startGame.setMultiplayerCorrelationId("");
        session.sendPacket(startGame);

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

    void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    public void updatePlayerList() {
        Set<Player> toAdd = new HashSet<>();
        Set<UUID> toRemove = new HashSet<>();
        Map<UUID, NukkitPlayerSession> availableSessions = new HashMap<>();

        for (NukkitPlayerSession session : getLevel().getEntityManager().getPlayers()) {
            if (session == this) continue;
            availableSessions.put(session.getUniqueId(), session);
        }

        for (NukkitPlayerSession session : availableSessions.values()) {
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
            session.sendPacket(list);
        }

        if (!toRemove.isEmpty()) {
            playersListed.removeAll(toRemove);

            PlayerListPacket list = new PlayerListPacket();
            list.setType(PlayerListPacket.Type.REMOVE);
            for (UUID uuid : toRemove) {
                list.getEntries().add(new PlayerListPacket.Entry(uuid));
            }
            session.sendPacket(list);
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
        session.sendPacket(packet);
    }

    private void sendCreativeInventory() {

    }

    public void sendPlayerInventory() {
        InventoryContentPacket initContents = new InventoryContentPacket();
        initContents.setWindowId(0x7b);
        session.sendPacket(initContents);

        // Because MCPE sends the hotbar as inventory, we have to add 9 more slots.
        InventoryContentPacket inventoryContents = new InventoryContentPacket();
        inventoryContents.setWindowId(0x00);
        inventoryContents.setContents(ItemUtil.toNetwork(Arrays.copyOf(inventory.getAllContents(), inventory.getInventoryType().getSize() + 9)));

        session.sendPacket(inventoryContents);

        MobEquipmentPacket mobEquipment = new MobEquipmentPacket();
        mobEquipment.setRuntimeEntityId(getEntityId());
        mobEquipment.setItem(ItemUtil.toNetwork(getInventory().getItemInHand().orElse(null)));
        mobEquipment.setInventorySlot(getInventory().getHeldHotbarSlot());

        session.sendPacket(mobEquipment);
    }

    void sendMovePlayer() {
        MovePlayerPacket packet = new MovePlayerPacket();

        packet.setRuntimeEntityId(getEntityId());
        packet.setPosition(getGamePosition());
        packet.setRotation(getRotation().toVector3f());
        packet.setOnGround(isOnGround());
        packet.setRidingRuntimeEntityId(0);
        if (isTeleported()) {
            packet.setMode(MovePlayerPacket.Mode.TELEPORT);
            packet.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
            packet.setUnknown0(1);
        } else {
            packet.setMode(MovePlayerPacket.Mode.NORMAL);
        }
        session.sendPacket(packet);
    }

    void sendEntityData() {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getMetadata().putAll(getMetadataFlags());
        session.sendPacket(packet);
    }

    void sendHealth() {
        Damageable damageable = ensureAndGet(Damageable.class);
        SetHealthPacket packet = new SetHealthPacket();
        packet.setHealth(Math.round(damageable.getFixedHealth()));
        session.sendPacket(packet);
    }

    private void sendGamerules() {
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        GameRules gameRules = level.getData().getGameRules();
        if (!(gameRules instanceof NukkitGameRules)) {
            throw new IllegalArgumentException("GameRules not of type NukkitGameRules");
        }
        packet.getGameRules().addAll(((NukkitGameRules) gameRules).getGameRules());
        session.sendPacket(packet);
    }

    private void sendAdventureSettings() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        NukkitAbilities abilities = data.getAbilities();

        AdventureSettingsPacket packet = new AdventureSettingsPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setPlayerFlags(abilities.getFlags());
        packet.setWorldFlags(abilities.getFlags2());
        packet.setCustomFlags(abilities.getCustomFlags());
        packet.setCommandPermission(data.getCommandPermission().ordinal());
        packet.setPlayerPermission(data.getPlayerPermission().ordinal());
        session.sendPacket(packet);
    }

    private void sendCommandsEnabled() {
        SetCommandsEnabledPacket packet = new SetCommandsEnabledPacket();
        packet.setCommandsEnabled(this.commandsEnabled);
        session.sendPacket(packet);
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

    @Override
    public BedrockPacket createAddEntityPacket() {
        PlayerDataComponent data = (PlayerDataComponent) ensureAndGet(PlayerData.class);
        AddPlayerPacket addPlayer = new AddPlayerPacket();
        addPlayer.setUuid(session.getAuthData().getIdentity());
        addPlayer.setUsername(session.getAuthData().getDisplayName());
        addPlayer.setUniqueEntityId(getEntityId());
        addPlayer.setRuntimeEntityId(getEntityId());
        addPlayer.setPlatformChatId("");
        addPlayer.setPosition(getGamePosition());
        addPlayer.setMotion(getMotion());
        addPlayer.setRotation(getRotation().toVector3f());
        addPlayer.setHand(ItemUtil.toNetwork(inventory.getItemInHand().orElse(null)));
        addPlayer.getMetadata().putAll(getMetadata());
        addPlayer.setPlayerFlags(data.getAbilities().getFlags());
        addPlayer.setCommandPermission(data.getCommandPermission().ordinal());
        addPlayer.setWorldFlags(data.getAbilities().getFlags2());
        addPlayer.setPlayerPermission(data.getPlayerPermission().ordinal());
        addPlayer.setCustomFlags(data.getAbilities().getCustomFlags());
        addPlayer.setDeviceId(session.getClientData().getDeviceId());
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
        session.sendPacket(packet);
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
        session.sendPacket(packet);
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
        packet.setSkinId(skin.getSkinId());
        packet.setSkinData(skin.getSkinData());
        packet.setCapeData(skin.getCapeData());
        packet.setGeometryName(skin.getGeometryName());
        packet.setGeometryData(skin.getGeometryData());
        packet.setOldSkinName(data.getSkin().getSkinId());
        packet.setNewSkinName(skin.getSkinId());
        packet.setUuid(getUniqueId());

        //TODO: Update player list

        ensureAndGet(PlayerData.class).setSkin(skin);
    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public void setButtonText(String text) {

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

    @Override
    public boolean isXboxAuthenticated() {
        return session.getAuthData().getXuid() != null;
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return DeviceOS.values()[session.getClientData().getDeviceOs()];
    }

    @Override
    public boolean isEducationEdition() {
        return session.getClientData().isEduMode();
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
            server.getCommandManager().executeCommand(NukkitPlayerSession.this, command);
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

    public BedrockPacketHandler getNetworkPacketHandler() {
        return new PlayerSessionPacketHandler(this);
    }

    @Override
    public void onInventorySlotChange(int slot, @Nullable ItemInstance oldItem, @Nullable ItemInstance newItem, @Nonnull Inventory inventory, @Nullable NukkitPlayerSession session) {
        log.debug("Adding item to inventory");
        OptionalInt optionalId = testInventoryChange(inventory);
        if (!optionalId.isPresent()) {
            return;
        }
        byte windowId = (byte) optionalId.getAsInt();

        // The session that sent the inventory change updates without the need of a packet.
        if (session != this) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setSlot(ItemUtil.toNetwork(newItem));
            packet.setInventorySlot(slot);
            packet.setWindowId(windowId);
            this.session.sendPacket(packet);
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
        packet.setContents(ItemUtil.toNetwork(contents));
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
        session.sendPacket(packet);
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
        session.sendPacket(packet);
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

    public NukkitPlayerInventory getInventory() {
        return this.inventory;
    }

    public Inventory getOpenInventory() {
        return this.openInventory;
    }

    void setOpenInventory(Inventory inventory) {
        this.openInventory = inventory;
    }

    @Override
    public void close() {

    }

    @Override
    public void onDisconnect(@Nullable String s) {

    }

    @Override
    public void onTimeout() {

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
            Preconditions.checkArgument(entity instanceof NukkitPlayerSession, "Entity was not of type NukkitPlayerSession");
            NukkitPlayerSession session = (NukkitPlayerSession) entity;

            DamageableComponent damageable = (DamageableComponent) session.ensureAndGet(Damageable.class);
            if (!session.spawned || session.getBedrockSession().isClosed() || damageable.isDead()) {
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

            session.updatePlayerList();
        }
    }
}
