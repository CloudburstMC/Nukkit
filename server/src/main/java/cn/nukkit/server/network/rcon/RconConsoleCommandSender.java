/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.rcon;

import cn.nukkit.api.command.sender.RemoteConsoleCommandSender;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionAttachment;
import cn.nukkit.api.permission.PermissionAttachmentInfo;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
