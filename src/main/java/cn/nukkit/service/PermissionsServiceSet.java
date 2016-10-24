package cn.nukkit.service;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import java.util.*;

public class PermissionsServiceSet implements PermissionService_v1 {

    private final Collection<PermissionService> services;

    public PermissionsServiceSet(Collection<PermissionService> services) {
        services = new ArrayList<>(services);
        services.removeIf(service -> !service.isEnabled());
        this.services = Collections.unmodifiableCollection(services);
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

    @Override
    public boolean has(IPlayer player, Level level, String perm) {
        if (!(player instanceof Player)) {
            for (PermissionService service : services) {
                if (service.has(player, level, perm)) {
                    return true;
                }
            }
            return false;
        }
        return ((Player) player).hasPermission(perm);
    }

    @Override
    public boolean isSet(IPlayer player, Level level, String perm) {
        if (!(player instanceof Player)) {
            for (PermissionService service : services) {
                if (service.isSet(player, level, perm)) {
                    return true;
                }
            }
            return false;
        }
        return ((Player) player).isPermissionSet(perm);
    }

    @Override
    public boolean add(IPlayer player, Level level, String perm, boolean value) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.add(player, level, perm, value);
        }
        return result;
    }

    @Override
    public boolean remove(IPlayer player, Level level, String perm) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.remove(player, level, perm);
        }
        return result;
    }

    @Override
    public boolean addTransient(IPlayer player, String permission, boolean value) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.addTransient(player, permission, value);
        }
        return result;
    }

    @Override
    public boolean removeTransient(IPlayer player, String permission) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.removeTransient(player, permission);
        }
        return result;
    }

    public boolean addTransient(Plugin plugin, Player player, String permission, boolean value) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions().values()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
                paInfo.getAttachment().setPermission(permission, value);
                return true;
            }
        }
        PermissionAttachment attach = player.addAttachment(plugin);
        attach.setPermission(permission, value);
        return true;
    }

    public boolean removeTransient(Plugin plugin, Player player, String permission) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions().values()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
                paInfo.getAttachment().unsetPermission(permission, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean groupHas(String group, Level level, String permission) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.groupHas(group, level, permission);
        }
        return result;
    }

    @Override
    public boolean groupAdd(String group, Level level, String permission) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.groupAdd(group, level, permission);
        }
        return result;
    }

    @Override
    public boolean groupRemove(String group, Level level, String permission) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.groupRemove(group, level, permission);
        }
        return result;
    }

    @Override
    public boolean playerInGroup(IPlayer player, Level level, String group) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.playerInGroup(player, level, group);
        }
        return result;
    }

    @Override
    public boolean playerAddGroup(IPlayer player, Level level, String group) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.playerAddGroup(player, level, group);
        }
        return result;
    }

    @Override
    public boolean playerRemoveGroup(IPlayer player, Level level, String group) {
        boolean result = false;
        for (PermissionService service : services) {
            result |= service.playerRemoveGroup(player, level, group);
        }
        return result;
    }

    @Override
    public String[] getPlayerGroups(IPlayer player, Level level) {
        if (services.size() == 0) {
            return new String[0];
        }
        if (services.size() == 1) {
            return services.iterator().next().getPlayerGroups(player, level);
        }
        Collection<String> groups = new HashSet<>();
        for (PermissionService service : services) {
            for (String group : service.getPlayerGroups(player, level)) {
                groups.add(group);
            }
        }
        return groups.toArray(new String[groups.size()]);
    }

    public String[] getPrimaryGroups(IPlayer player, Level level) {
        if (services.size() == 0) {
            return new String[0];
        }
        if (services.size() == 1) {
            String group = services.iterator().next().getPrimaryGroup(player, level);
            if (group == null) {
                return new String[0];
            }
            return new String[] {group};
        }
        HashSet<String> groups = new HashSet<>();
        for (PermissionService service : services) {
            String group = service.getPrimaryGroup(player, level);
            if (group != null) {
                groups.add(group);
            }
        }
        return groups.toArray(new String[groups.size()]);
    }

    @Deprecated
    @Override
    public String getPrimaryGroup(IPlayer player, Level level) {
        for (PermissionService service : services) {
            String group = service.getPrimaryGroup(player, level);
            if (group != null) {
                return group;
            }
        }
        return null;
    }

    @Override
    public String[] getGroups() {
        if (services.size() == 0) {
            return new String[0];
        }
        if (services.size() == 1) {
            return services.iterator().next().getGroups();
        }
        Collection<String> groups = new HashSet<>();
        for (PermissionService service : services) {
            for (String group : service.getGroups()) {
                groups.add(group);
            }
        }
        return groups.toArray(new String[groups.size()]);
    }
}
