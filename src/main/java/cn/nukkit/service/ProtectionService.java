package cn.nukkit.service;

import cn.nukkit.IPlayer;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Position;

public interface ProtectionService extends Service {
    public boolean isAllowedEntry(IPlayer player, Position pos, PlayerTeleportEvent.TeleportCause cause);

    public boolean isAllowedInteract(IPlayer player, Position pos, int face, int action);

    public boolean isAllowedModification(IPlayer player, Position pos);
}
