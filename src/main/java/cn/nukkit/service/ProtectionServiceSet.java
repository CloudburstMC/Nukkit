package cn.nukkit.service;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.RegisteredListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ProtectionServiceSet implements ProtectionService_v1 {

    private final Collection<ProtectionService> services;

    private static Item AIR = new ItemBlock(new BlockAir(), 0, 0);
    private final PlayerTeleportEvent teleportEvent;
    private final PlayerInteractEvent interactEvent;
    private final BlockBreakEvent breakEvent;
    private final ArrayList<RegisteredListener> teleportListeners;
    private final ArrayList<RegisteredListener> interactListeners;
    private final ArrayList<RegisteredListener> breakListeners;
    private final Location mutableLocation = new Location();

    public ProtectionServiceSet(Collection<ProtectionService> services) {
        services = new ArrayList<>(services);
        services.removeIf(service -> !service.isEnabled());
        this.services = Collections.unmodifiableCollection(services);

        this.teleportEvent = new PlayerTeleportEvent(null, null, null, null);
        this.interactEvent = new PlayerInteractEvent(null, null, null, 0, 0);
        this.breakEvent = new BlockBreakEvent(null, null, null, false, false);

        this.teleportListeners = PlayerTeleportEvent.getHandlers().getRegisteredListeners(EventPriority.HIGHEST, EventPriority.HIGH, EventPriority.NORMAL, EventPriority.LOW, EventPriority.LOWEST);
        this.interactListeners = PlayerInteractEvent.getHandlers().getRegisteredListeners(EventPriority.HIGHEST, EventPriority.HIGH, EventPriority.NORMAL, EventPriority.LOW, EventPriority.LOWEST);
        this.breakListeners = BlockBreakEvent.getHandlers().getRegisteredListeners(EventPriority.HIGHEST, EventPriority.HIGH, EventPriority.NORMAL, EventPriority.LOW, EventPriority.LOWEST);
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Nukkit";
    }

    public boolean isAllowedEntry(IPlayer player, Position pos) {
        return isAllowedEntry(player, pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isAllowedEntry(IPlayer player, Position to, PlayerTeleportEvent.TeleportCause cause) {
        if (!(player instanceof Player)) {
            for (ProtectionService service : services) {
                if (!service.isAllowedEntry(player, to, cause)) {
                    return false;
                }
            }
            return true;
        }
        if (teleportListeners.isEmpty()) {
            return true;
        }
        Player nukkitPlayer = (Player) player;
        this.mutableLocation.setComponents(to.x, to.y, to.z);
        this.mutableLocation.setLevel(to.level);
        this.teleportEvent.reset(nukkitPlayer, nukkitPlayer, this.mutableLocation, cause);
        for (RegisteredListener listener : this.teleportListeners) {
            listener.callEvent(this.teleportEvent);
        }
        if (this.teleportEvent.isCancelled()) {
            return false;
        }
        return true;
    }

    public boolean isAllowedInteract(IPlayer player, Position pos) {
        return isAllowedInteract(player, pos, 0, PlayerInteractEvent.RIGHT_CLICK_BLOCK);
    }

    @Override
    public boolean isAllowedInteract(IPlayer player, Position pos, int face, int action) {
        if (!(player instanceof Player)) {
            for (ProtectionService service : services) {
                if (!service.isAllowedInteract(player, pos, face, action)) {
                    return false;
                }
            }
            return true;
        }
        if (interactListeners.isEmpty()) {
            return true;
        }
        Player nukkitPlayer = (Player) player;
        this.interactEvent.reset(nukkitPlayer, AIR, pos.getLevelBlock(), face, action);
        for (RegisteredListener listener : this.interactListeners) {
            listener.callEvent(this.interactEvent);
        }
        if (this.interactEvent.isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAllowedModification(IPlayer player, Position pos) {
        if (!(player instanceof Player)) {
            for (ProtectionService service : services) {
                if (!service.isAllowedModification(player, pos)) {
                    return false;
                }
            }
            return true;
        }
        if (breakListeners.isEmpty()) {
            return true;
        }
        Player nukkitPlayer = (Player) player;
        this.breakEvent.reset(nukkitPlayer, pos.getLevelBlock(), AIR, false, false, false);
        for (RegisteredListener listener : this.breakListeners) {
            listener.callEvent(this.breakEvent);
        }
        if (this.breakEvent.isCancelled()) {
            return false;
        }
        return true;
    }
}