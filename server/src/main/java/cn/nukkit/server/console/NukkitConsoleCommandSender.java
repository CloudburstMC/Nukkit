package cn.nukkit.server.console;

import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionAttachment;
import cn.nukkit.api.permission.PermissionAttachmentInfo;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.Map;

@Log4j2
public class NukkitConsoleCommandSender implements ConsoleCommandSender {
    private final NukkitServer server;

    public NukkitConsoleCommandSender(NukkitServer server) {
        this.server = server;
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        Preconditions.checkNotNull(text, "text");
        log.info(text);
    }

    @Override
    public void sendMessage(@Nonnull Message text) {
        Preconditions.checkNotNull(text, "text");
        if (text instanceof TranslationMessage) {
            log.info(TranslatableMessage.of((TranslationMessage) text));
            return;
        }
        log.info(text.getMessage());
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public NukkitServer getServer() {
        return server;
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
}
