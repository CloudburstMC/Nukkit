package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Position position;
    
    private Position spawnBlock;
    
    private Position originalSpawnPosition;
    
    private boolean spawnBlockAvailable;

    private boolean firstSpawn;
    
    private boolean keepRespawnBlockPosition;
    private boolean keepRespawnPosition;
    private boolean sendInvalidRespawnBlockMessage = true;
    private boolean consumeCharge = true;

    public PlayerRespawnEvent(Player player, Position position) {
        this(player, position, false);
    }

    public PlayerRespawnEvent(Player player, Position position, boolean firstSpawn) {
        this.player = player;
        this.position = position;
        this.firstSpawn = firstSpawn;
    }

    public Position getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Position position) {
        this.position = position;
    }

    public boolean isFirstSpawn() {
        return firstSpawn;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Position getRespawnBlockPosition() {
        return spawnBlock;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRespawnBlockPosition(Position spawnBlock) {
        this.spawnBlock = spawnBlock;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isRespawnBlockAvailable() {
        return spawnBlockAvailable;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRespawnBlockAvailable(boolean spawnBlockAvailable) {
        this.spawnBlockAvailable = spawnBlockAvailable;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Position getOriginalRespawnPosition() {
        return originalSpawnPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOriginalRespawnPosition(Position originalSpawnPosition) {
        this.originalSpawnPosition = originalSpawnPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isKeepRespawnBlockPosition() {
        return keepRespawnBlockPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setKeepRespawnBlockPosition(boolean keepRespawnBlockPosition) {
        this.keepRespawnBlockPosition = keepRespawnBlockPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isKeepRespawnPosition() {
        return keepRespawnPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setKeepRespawnPosition(boolean keepRespawnPosition) {
        this.keepRespawnPosition = keepRespawnPosition;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSendInvalidRespawnBlockMessage() {
        return sendInvalidRespawnBlockMessage;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSendInvalidRespawnBlockMessage(boolean sendInvalidRespawnBlockMessage) {
        this.sendInvalidRespawnBlockMessage = sendInvalidRespawnBlockMessage;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isConsumeCharge() {
        return consumeCharge;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setConsumeCharge(boolean consumeCharge) {
        this.consumeCharge = consumeCharge;
    }
}
