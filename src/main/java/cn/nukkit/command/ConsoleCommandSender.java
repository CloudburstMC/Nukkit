package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.locale.TextContainer;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class ConsoleCommandSender implements CommandSender {

    private final PermissibleBase perm;

    public ConsoleCommandSender() {
        this.perm = new PermissibleBase(this);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.perm.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public boolean isPlayer() {
        return false;
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    @Override
    public void sendMessage(String message) {
        message = this.getServer().getLanguage().translate(message);
        for (String line : message.trim().split("\n")) {
            log.info(line);
        }
    }

    @Override
    public void sendMessage(TextContainer message) {
        Object[] args = null;
        if (message instanceof TranslationContainer) {
            args = ((TranslationContainer) message).getParameters();
        }
        this.sendMessage(this.getServer().getLanguage().translate(message.getText(), args));
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }
}
