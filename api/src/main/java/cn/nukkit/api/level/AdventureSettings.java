package cn.nukkit.api.level;

public interface AdventureSettings {

    CommandPermission getCommandPermission();

    void setCommandPermission(CommandPermission commandPermission);

    PlayerPermission getPlayerPermission();

    void setPlayerPermission(PlayerPermission playerPermission);

    boolean isWorldImmutable();

    void setWorldImmutable(boolean worldImmutable);

    boolean isPvpEnabled();

    void setPvpEnabled(boolean pvpEnabled);

    boolean isPvmEnabled();

    void setPvmEnabled(boolean pvmEnabled);

    boolean isMvpEnabled();

    void setMvpEnabled(boolean mvpEnabled);

    boolean isAutoJumpEnabled();

    void setAutoJumpEnabled(boolean autoJumpEnabled);

    boolean isAllowedFlight();

    void setAllowedFlight(boolean allowedFlight);

    boolean isNoClipEnabled();

    void setNoClipEnabled(boolean noClipEnabled);

    boolean isWorldBuilder();

    void setWorldBuilder(boolean worldBuilder);

    boolean isFlying();

    void setFlying(boolean flying);

    boolean isMuted();

    void setMuted(boolean muted);

    // Action Permissions

    boolean canPlaceAndDestroy();

    void setPlaceAndDestroy(boolean placeAndDestroy);

    boolean canInteractWithDoorsAndSwitches();

    void setInteractWithDoorsAndSwitches(boolean interactWithDoorsAndSwitches);

    boolean canOpenContainers();

    void setOpenContainers(boolean openContainers);

    boolean canAttackPlayers();

    void setAttackPlayers(boolean attackPlayers);

    boolean canAttackMobs();

    void setAttackMobs(boolean attackMobs);

    boolean isOperator();

    void setOperator(boolean operator);

    boolean canTeleport();

    void setTeleport(boolean teleport);

    enum PlayerPermission {
        VISITOR,
        MEMBER,
        OPERATOR,
        CUSTOM
    }

    enum CommandPermission {
        NORMAL,
        OPERATOR,
        HOST,
        AUTOMATION,
        ADMIN
    }
}
