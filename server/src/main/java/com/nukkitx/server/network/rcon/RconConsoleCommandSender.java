package com.nukkitx.server.network.rcon;

import com.nukkitx.api.command.sender.RemoteConsoleCommandSender;
import com.nukkitx.api.message.Message;
import com.nukkitx.api.permission.Permission;
import com.nukkitx.api.permission.PermissionAttachment;
import com.nukkitx.api.permission.PermissionAttachmentInfo;
import com.nukkitx.api.plugin.Plugin;
import com.nukkitx.server.NukkitServer;

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
