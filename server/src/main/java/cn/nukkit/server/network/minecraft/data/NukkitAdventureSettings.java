package cn.nukkit.server.network.minecraft.data;

import cn.nukkit.api.level.AdventureSettings;

import java.util.BitSet;

public class NukkitAdventureSettings implements AdventureSettings {
    private BitSet worldPermissions;
    private CommandPermission commandPermission;
    private BitSet actionPermissions;
    private BitSet customStoredPermissions;
    private PlayerPermission playerPermission;
    private boolean stale;

    public NukkitAdventureSettings(int worldPermissions, CommandPermission commandPermission, int actionPermissions, int customStoredPermissions, PlayerPermission playerPermission) {
        setAll(worldPermissions, commandPermission, actionPermissions, customStoredPermissions, playerPermission);
        stale = true;
    }

    public void setAll(int worldPermissions, CommandPermission commandPermission, int actionPermissions, int customStoredPermissions, PlayerPermission playerPermission) {
        this.worldPermissions = BitSet.valueOf(new long[]{worldPermissions});
        this.actionPermissions = BitSet.valueOf(new long[]{actionPermissions});
        this.customStoredPermissions = BitSet.valueOf(new long[]{customStoredPermissions});
        this.commandPermission = commandPermission;
        this.playerPermission = playerPermission;
    }

    public boolean isStale() {
        return stale;
    }

    public int getWorldPermissions() {
        return (int) worldPermissions.toLongArray()[0];
    }

    public int getActionPermissions() {
        return (int) actionPermissions.toLongArray()[0];
    }

    public int getCustomStoredPermissions() {
        return (int) customStoredPermissions.toLongArray()[0];
    }

    public CommandPermission getCommandPermission() {
        return commandPermission;
    }

    public void setCommandPermission(CommandPermission commandPermission) {
        stale = true;
        this.commandPermission = commandPermission;
    }

    public PlayerPermission getPlayerPermission() {
        return playerPermission;
    }

    public void setPlayerPermission(PlayerPermission playerPermission) {
        stale = true;
        this.playerPermission = playerPermission;
    }

    // World Permissions
    @Override
    public boolean isWorldImmutable() {
        return worldPermissions.get(0);
    }

    @Override
    public void setWorldImmutable(boolean worldImmutable) {
        stale = true;
        worldPermissions.set(0, worldImmutable);
    }

    @Override
    public boolean isPvpEnabled() {
        return worldPermissions.get(1);
    }

    @Override
    public void setPvpEnabled(boolean pvpEnabled) {
        stale = true;
        worldPermissions.set(1, pvpEnabled);
    }

    @Override
    public boolean isPvmEnabled() {
        return worldPermissions.get(2);
    }

    @Override
    public void setPvmEnabled(boolean pvmEnabled) {
        worldPermissions.set(2, pvmEnabled);
    }

    @Override
    public boolean isMvpEnabled() {
        return worldPermissions.get(3);
    }

    @Override
    public void setMvpEnabled(boolean mvpEnabled) {
        worldPermissions.set(3, mvpEnabled);
    }

    @Override
    public boolean isAutoJumpEnabled() {
        return worldPermissions.get(5);
    }

    @Override
    public void setAutoJumpEnabled(boolean autoJumpEnabled) {
        stale = true;
        worldPermissions.set(5, autoJumpEnabled);
    }

    @Override
    public boolean isAllowedFlight() {
        return worldPermissions.get(6);
    }

    @Override
    public void setAllowedFlight(boolean allowedFlight) {
        stale = true;
        worldPermissions.set(6, allowedFlight);
    }

    @Override
    public boolean isNoClipEnabled() {
        return worldPermissions.get(7);
    }

    @Override
    public void setNoClipEnabled(boolean noClipEnabled) {
        stale = true;
        worldPermissions.set(7, noClipEnabled);
    }

    @Override
    public boolean isWorldBuilder() {
        return worldPermissions.get(8);
    }

    @Override
    public void setWorldBuilder(boolean worldBuilder) {
        stale = true;
        worldPermissions.set(8, worldBuilder);
    }

    @Override
    public boolean isFlying() {
        return worldPermissions.get(9);
    }

    @Override
    public void setFlying(boolean flying) {
        stale = true;
        worldPermissions.set(9, flying);
    }

    @Override
    public boolean isMuted() {
        return worldPermissions.get(10);
    }

    @Override
    public void setMuted(boolean muted) {
        stale = true;
        worldPermissions.set(9, muted);
    }

    // Action Permissions

    @Override
    public boolean canPlaceAndDestroy() {
        return actionPermissions.get(0);
    }

    @Override
    public void setPlaceAndDestroy(boolean placeAndDestroy) {
        stale = true;
        actionPermissions.set(0, placeAndDestroy);
    }

    @Override
    public boolean canInteractWithDoorsAndSwitches() {
        return actionPermissions.get(1);
    }

    @Override
    public void setInteractWithDoorsAndSwitches(boolean interactWithDoorsAndSwitches) {
        stale = true;
        actionPermissions.set(1, interactWithDoorsAndSwitches);
    }

    @Override
    public boolean canOpenContainers() {
        return actionPermissions.get(2);
    }

    @Override
    public void setOpenContainers(boolean openContainers) {
        stale = true;
        actionPermissions.set(2, openContainers);
    }

    @Override
    public boolean canAttackPlayers() {
        return actionPermissions.get(3);
    }

    @Override
    public void setAttackPlayers(boolean attackPlayers) {
        stale = true;
        actionPermissions.set(3, attackPlayers);
    }

    @Override
    public boolean canAttackMobs() {
        return actionPermissions.get(4);
    }

    @Override
    public void setAttackMobs(boolean attackMobs) {
        stale = true;
        actionPermissions.set(4, attackMobs);
    }

    @Override
    public boolean isOperator() {
        return actionPermissions.get(5);
    }

    @Override
    public void setOperator(boolean operator) {
        stale = true;
        actionPermissions.set(5, operator);
    }

    @Override
    public boolean canTeleport() {
        return actionPermissions.get(6);
    }

    @Override
    public void setTeleport(boolean teleport) {
        stale = true;
        actionPermissions.set(6, teleport);
    }
}
