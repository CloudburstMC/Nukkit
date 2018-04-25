package cn.nukkit.server.network.rcon;

import cn.nukkit.api.command.sender.RemoteConsoleCommandSender;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionAttachment;
import cn.nukkit.api.permission.PermissionAttachmentInfo;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RconConsoleCommandSender implements RemoteConsoleCommandSender {
    private final NukkitServer server;
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();


    public RconConsoleCommandSender(NukkitServer server) {
        this.server = server;
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        messageQueue.add(text);
    }

    @Override
    public void sendMessage(@Nonnull Message text) {
        sendMessage(server.getLocaleManager().getMessage(text));
    }

    @Override
    public String getName() {
        return "RCON";
    }

    @Override
    public NukkitServer getServer() {
        return server;
    }

    @Override
    public Locale getLocale() {
        return server.getLanguage();
    }

    public List<String> getMessages() {
        List<String> messages = new ArrayList<>();
        String message;
        while ((message = messageQueue.poll()) != null) {
            messages.add(message);
        }
        return messages;
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
