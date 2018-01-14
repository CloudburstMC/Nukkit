package cn.nukkit.server.network.minecraft.session;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.Player;
import cn.nukkit.api.Skin;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.PlayerData;
import cn.nukkit.api.event.player.PlayerKickEvent;
import cn.nukkit.api.form.window.FormWindow;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.level.AdventureSettings;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.resourcepack.BehaviorPack;
import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.api.util.data.DeviceOS;
import cn.nukkit.server.entity.EntityTypeData;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.entity.component.PlayerDataComponent;
import cn.nukkit.server.inventory.transaction.*;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.data.NukkitAdventureSettings;
import cn.nukkit.server.network.minecraft.packet.*;
import cn.nukkit.server.resourcepack.loader.file.PackFile;
import com.flowpowered.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerSession extends LivingEntity implements Player {
    private final MinecraftSession session;
    private final Level level;
    private String displayName;
    private String playerListName;

    public PlayerSession(MinecraftSession session, Level level) {
        super(EntityTypeData.PLAYER, level, level.getLevelSettings().getSpawnPosition(), session.getServer(), 20);
        this.session = session;
        this.level = level;
        playerListName = displayName = session.getAuthData().getDisplayName();

        // Register components
        registerComponent(PlayerData.class, new PlayerDataComponent());
    }

    private void startGame() {
        //PlayerLoginEvent event = new PlayerLoginEvent(this, );

        StartGamePacket startGame = new StartGamePacket();
        startGame.setUniqueEntityId(getUniqueEntityId());
        startGame.setRuntimeEntityId(getRuntimeEntityId());
        startGame.setGamemode(level.getDefaultGamemode());
        startGame.setPlayerPosition(level.getLevelSettings().getSpawnPosition());
        startGame.setRotation(getRotation().getBodyRotation());
        startGame.setLevelSettings(level.getLevelSettings());
        startGame.setLevelId(level.getId());
        startGame.setWorldName(level.getName());
        startGame.setPremiumWorldTemplateId("");
        startGame.setTrial(false);
        startGame.setCurrentTick(level.getTick());
        startGame.setEnchantmentSeed(enchantmentSeed);
    }

    private void sendInventory() {

    }

    private void sendCreativeInventory() {

    }

    private void sendHealth() {
        Damageable damageable = ensureAndGet(Damageable.class);
        SetHealthPacket packet = new SetHealthPacket();
        packet.setHealth(damageable.getRoundedHealth());
        session.addToSendQueue(packet);
    }

    private void sendGamerules() {
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        packet.getRules().addAll(level.getLevelSettings().getGameRules());
        session.addToSendQueue(packet);
    }

    private void sendAdventureSettings() {
        PlayerData data = ensureAndGet(PlayerData.class);
        NukkitAdventureSettings settings = (NukkitAdventureSettings) data.getAdventureSettings();
        AdventureSettingsPacket packet = new AdventureSettingsPacket();
        packet.setWorldPermissions(settings.getWorldPermissions());
        packet.setCommandPermission(settings.getCommandPermission());
        packet.setActionPermissions(settings.getActionPermissions());
        packet.setPlayerPermission(settings.getPlayerPermission());
        packet.setCustomStoredPermissions(settings.getCustomStoredPermissions());
        packet.setUniqueEntityId(getUniqueEntityId());
        session.addToSendQueue(packet);
    }



    @Nonnull
    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @Override
    public void setDisplayName(@Nullable String name) {
        this.displayName = name;
    }

    @Nonnull
    @Override
    public Optional<String> getPlayerListName() {
        return Optional.ofNullable(playerListName);
    }

    @Override
    public void setPlayerListName(@Nullable String name) {
        playerListName = name;
    }

    @Nonnull
    @Override
    public GameMode getGameMode() {
        return ensureAndGet(PlayerData.class).getGameMode();
    }

    @Override
    public void setGameMode(@Nullable GameMode gameMode) {
        ensureAndGet(PlayerData.class).setGamemode(gameMode == null ? GameMode.SURVIVAL : gameMode);
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean value) {

    }

    @Override
    public int getExpToLevel() {
        return 0;
    }

    @Override
    public void addWindow(Inventory inventory) {

    }

    @Override
    public void addWindow(Inventory inventory, int forceId) {

    }

    @Override
    public int getWindowId(Inventory inventory) {
        return 0;
    }

    @Override
    public Inventory getWindowById() {
        return null;
    }

    @Override
    public void removeWindow(Inventory inventory) {

    }

    @Override
    public void removeAllWindows() {

    }

    @Override
    public void sendAllInventories() {

    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void kick() {

    }

    @Override
    public void kick(String reason) {

    }

    @Override
    public void kick(PlayerKickEvent.Reason reason) {

    }

    @Override
    public void chat(String message) {

    }

    @Override
    public void saveData() {

    }

    @Override
    public void saveData(boolean async) {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void addExp(int exp) {

    }

    @Override
    public int getExp() {
        return 0;
    }

    @Override
    public void addExpLevel(int level) {

    }

    @Override
    public int getExpLevel() {
        return 0;
    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public void setAllowFlight(boolean value) {

    }

    @Override
    public void hidePlayer(Player player) {

    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public boolean canSee(Player player) {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public void setFlying(boolean value) {

    }

    @Override
    public void setMovementSpeed() {

    }

    @Override
    public int getMovementSpeed() {
        return 0;
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
    public int showFormWindow(FormWindow formWindow) {
        return 0;
    }

    @Override
    public int showFormWindow(FormWindow formWindow, int forceId) {
        return 0;
    }

    @Override
    public int addServerSettings(FormWindow formWindow) {
        return 0;
    }

    @Override
    public void setCheckMovement(boolean value) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public void transfer(InetSocketAddress address) {

    }

    @Override
    public boolean isBreakingBlock() {
        return false;
    }

    @Override
    public void showXboxProfile(String xuid) {

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
    public boolean isEnabledClientCommand() {
        return false;
    }

    @Override
    public void setEnabledClientCommand(boolean value) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Skin getSkin() {
        return null;
    }

    @Override
    public void setSkin(Skin skin) {

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
    public void sleepOn(Vector3d pos) {

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
    public boolean isSurvival() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isAdventure() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public Entity getEntityPlayerLookingAt() {
        return null;
    }

    @Override
    public int getViewDistance() {
        return 0;
    }

    @Override
    public void setViewDistance(int distance) {

    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void sendTranslation(String message, String... parameters) {

    }

    @Override
    public void sendPopup(String popup) {

    }

    @Override
    public void sendTip(String tip) {

    }

    @Override
    public void sendActionBar(String message) {

    }

    @Override
    public void sendActionBar(String message, int fadein, int duration, int fadeout) {

    }

    @Override
    public boolean dropItem(ItemStack item) {
        return false;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Nonnull
    @Override
    public String getName() {
        return session.getAuthData().getDisplayName();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return null;
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
        return false;
    }

    @Override
    public void setWhitelisted(boolean value) {

    }


    public PlayerPacketHandler getPacketHandler() {
        return new PlayerPacketHandler();
    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return Optional.empty();
    }

    @Override
    public boolean isXboxAuthenticated() {
        return false;
    }

    @Nonnull
    @Override
    public Optional<String> getXuid() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return null;
    }

    @Override
    public boolean isEducationEdition() {
        return false;
    }

    @Nonnull
    @Override
    public String getDeviceModel() {
        return null;
    }

    @Nonnull
    @Override
    public String getGameVersion() {
        return null;
    }

    @Nonnull
    @Override
    public String getServerAddress() {
        return null;
    }

    @Nonnull
    @Override
    public Optional<String> getActiveDirectoryRole() {
        return Optional.empty();
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean sneaking) {

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

    private class PlayerPacketHandler extends NetworkPacketHandler {

        @Override
        public void handle(AdventureSettingsPacket packet) {
            NukkitAdventureSettings settings = (NukkitAdventureSettings) ensureAndGet(PlayerData.class).getAdventureSettings();

            if (settings.getPlayerPermission() == AdventureSettings.PlayerPermission.OPERATOR) {
                settings.setAll(
                        packet.getWorldPermissions(),
                        packet.getCommandPermission(),
                        packet.getActionPermissions(),
                        packet.getCustomStoredPermissions(),
                        packet.getPlayerPermission()
                );
            }
            // TODO: Check that the player has permission to change these settings.
        }

        @Override
        public void handle(AnimatePacket packet) {

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
        public void handle(ClientToServerHandshakePacket packet) {

        }

        @Override
        public void handle(CommandBlockUpdatePacket packet) {

        }

        @Override
        public void handle(CommandRequestPacket packet) {

        }

        @Override
        public void handle(ContainerClosePacket packet) {

        }

        @Override
        public void handle(CraftingEventPacket packet) {

        }

        @Override
        public void handle(EntityEventPacket packet) {

        }

        @Override
        public void handle(EntityFallPacket packet) {

        }

        @Override
        public void handle(EntityPickRequestPacket packet) {

        }

        @Override
        public void handle(EventPacket packet) {

        }

        @Override
        public void handle(InteractPacket packet) {

        }

        @Override
        public void handle(InventoryContentPacket packet) {

        }

        @Override
        public void handle(InventorySlotPacket packet) {

        }

        @Override
        public void handle(InventoryTransactionPacket packet) {

        }

        @Override
        public void handle(ItemFrameDropItemPacket packet) {

        }

        @Override
        public void handle(LevelSoundEventPacket packet) {

        }

        @Override
        public void handle(MapInfoRequestPacket packet) {

        }

        @Override
        public void handle(MobArmorEquipmentPacket packet) {

        }

        @Override
        public void handle(MobEquipmentPacket packet) {

        }

        @Override
        public void handle(ModalFormResponsePacket packet) {

        }

        @Override
        public void handle(MoveEntityPacket packet) {

        }

        @Override
        public void handle(MovePlayerPacket packet) {

        }

        @Override
        public void handle(PhotoTransferPacket packet) {

        }

        @Override
        public void handle(PlayerActionPacket packet) {

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

        }

        @Override
        public void handle(ResourcePackChunkRequestPacket packet) {
            Optional<ResourcePack> optionalPack = session.getServer().getResourcePackManager().getPackById(packet.getPackId());
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
            switch (packet.getStatus()) {
                case HAVE_ALL_PACKS:
                    ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                    stackPacket.setForcedToAccept(session.getServer().getServerProperties().isResourcesForced());
                    packet.getPackIds().forEach(id -> session.getServer().getResourcePackManager().getPackById(id));
                    for (UUID id: packet.getPackIds()) {
                        Optional<ResourcePack> optionalPack = session.getServer().getResourcePackManager().getPackById(id);
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
                    return;
                case SEND_PACKS:
                    for (UUID packId: packet.getPackIds()) {
                        Optional<ResourcePack> optionalPack = session.getServer().getResourcePackManager().getPackById(packId);
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
                    if (session.getServer().getServerProperties().isResourcesForced()) {
                        session.disconnect("disconnectionScreen.resourcePack");
                        return;
                    }
                    // Fall through
                default:
                    startGame();
            }
        }

        @Override
        public void handle(RespawnPacket packet) {

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
        public void handle(SetEntityMotionPacket packet) {

        }

        @Override
        public void handle(SetPlayerGameTypePacket packet) {

        }

        @Override
        public void handle(SubClientLoginPacket packet) {

        }

        @Override
        public void handle(TextPacket textPacket) {

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
}
