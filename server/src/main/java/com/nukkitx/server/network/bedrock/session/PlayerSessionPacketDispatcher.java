package com.nukkitx.server.network.bedrock.session;

import com.nukkitx.api.level.GameRules;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.LevelData;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.server.container.manager.ContainerManager;
import com.nukkitx.server.inventory.Inventories;
import com.nukkitx.server.inventory.transaction.InventoryTransaction;
import com.nukkitx.server.inventory.transaction.InventoryTransactions;
import com.nukkitx.server.level.NukkitGameRules;
import com.nukkitx.server.level.NukkitLevel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PlayerSessionPacketDispatcher {
    private final PlayerSession player;

    public void sendStartGame() {
        Level level = player.getLevel();
        StartGamePacket startGame = new StartGamePacket();
        startGame.setUniqueEntityId(player.getEntityId());
        startGame.setRuntimeEntityId(player.getEntityId());
        startGame.setPlayerGamemode(player.getGameMode().ordinal());
        startGame.setPlayerPosition(player.getPosition());
        startGame.setRotation(player.getRotation().toVector2f());
        // Level Settings
        LevelData data = level.getData();
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
        startGame.setBroadcastingToLan(true);
        startGame.getGamerules().addAll(((NukkitGameRules) data.getGameRules()).getGameRules());
        startGame.setBroadcastingToLan(data.isBroadcastingToLan());
        startGame.setPlatformBroadcastMode(GamePublishSetting.FRIENDS_OF_FRIENDS);
        startGame.setXblBroadcastMode(GamePublishSetting.FRIENDS_OF_FRIENDS);
        startGame.setCommandsEnabled(data.isCommandsEnabled());
        startGame.setTexturePacksRequired(data.isTexturepacksRequired());
        startGame.setBonusChestEnabled(data.isBonusChestEnabled());
        startGame.setStartingWithMap(data.isStartingWithMap());
        startGame.setTrustingPlayers(data.isTrustingPlayers());
        startGame.setDefaultPlayerPermission(data.getDefaultPlayerPermission().ordinal());
        startGame.setServerChunkTickRange(data.getServerChunkTickRange());
        startGame.setBehaviorPackLocked(data.isBehaviorPackLocked());
        startGame.setResourcePackLocked(data.isResourcePackLocked());
        startGame.setFromLockedWorldTemplate(data.isFromLockedWorldTemplate());
        startGame.setUsingMsaGamertagsOnly(data.isUsingMsaGamertagsOnly());
        startGame.setFromWorldTemplate(false);
        startGame.setWorldTemplateOptionLocked(false);

        startGame.setLevelId(level.getId());
        startGame.setWorldName("world"); //level.getCustomName()
        startGame.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        startGame.setCurrentTick(level.getCurrentTick());
        startGame.setEnchantmentSeed(player.getEnchantmentSeed());
        startGame.setMultiplayerCorrelationId("");

        startGame.setCachedPalette(NukkitLevel.getPaletteManager().getCachedPallete());

        player.getBedrockSession().sendPacketImmediately(startGame);
    }

    public void sendCommandsEnabled() {
        SetCommandsEnabledPacket packet = new SetCommandsEnabledPacket();
        packet.setCommandsEnabled(player.hasCommandsEnabled());
        player.sendNetworkPacket(packet);
    }

    public void sendInventory() {
        sendInventory(false);
    }

    public void sendGamerules() {
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        GameRules gameRules = player.getLevel().getData().getGameRules();
        if (!(gameRules instanceof NukkitGameRules)) {
            throw new IllegalArgumentException("GameRules not of type NukkitGameRules");
        }
        packet.getGameRules().addAll(((NukkitGameRules) gameRules).getGameRules());
        player.sendNetworkPacket(packet);
    }

    public void sendEmptyInventory() {
        InventoryContentPacket empty = new InventoryContentPacket();
        empty.setContents(new ItemData[0]);
        empty.setContainerId(ContainerId.NONE);
        player.sendNetworkPacket(empty);
    }

    public void sendInventory(boolean selectHotbarSlot) {
        player.sendNetworkPacket(Inventories.newInventoryContentPacket(player, ContainerId.INVENTORY));
        //player.sendNetworkPacket(Inventories.newInventoryContentPacket(player, ContainerId.ARMOR));
        player.sendNetworkPacket(Inventories.newInventoryContentPacket(player, ContainerId.CURSOR));
        //player.sendNetworkPacket(Inventories.newInventoryContentPacket(player, ContainerId.OFFHAND));

        PlayerHotbarPacket playerHotbar = new PlayerHotbarPacket();
        playerHotbar.setContainerId(player.getInventory().getSelectedId());
        playerHotbar.setSelectedHotbarSlot(player.getInventory().getSelectedSlot());
        playerHotbar.setSelectHotbarSlot(selectHotbarSlot);
        player.sendNetworkPacket(playerHotbar);

        // TODO: 20/12/2018 ContainerManger
    }

    public void sendContainer(ContainerManager containerManager) {
        player.sendNetworkPacket(Inventories.newInventoryContentPacket(player, containerManager.getContainerId()));
    }

    public void sendMovePlayer() {
        MovePlayerPacket packet = new MovePlayerPacket();

        packet.setRuntimeEntityId(player.getEntityId());
        packet.setPosition(player.getGamePosition());
        packet.setRotation(player.getRotation().toVector3f());
        packet.setOnGround(player.isOnGround());
        packet.setRidingRuntimeEntityId(0);
        if (player.isTeleported()) {
            packet.setMode(MovePlayerPacket.Mode.TELEPORT);
            packet.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
            packet.setUnknown0(1); // need help
        } else {
            packet.setMode(MovePlayerPacket.Mode.NORMAL);
        }
        player.sendNetworkPacket(packet);
    }

    public void sendInventoryTransaction(InventoryTransaction inventoryTransaction) {
        player.sendNetworkPacket(InventoryTransactions.toPacket(inventoryTransaction));
    }
}
