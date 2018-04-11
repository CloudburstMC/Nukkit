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

package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.ChatMessage;
import cn.nukkit.api.message.Message;

public class PlayerQuitEvent implements PlayerEvent {
    private final Player player;
    private final String reason;
    private Message quitMessage;
    private boolean autoSave = true;

    public PlayerQuitEvent(Player player, Message quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(Player player, Message quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(Player player, String quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave, String reason) {
        this(player, new ChatMessage(quitMessage), autoSave, reason);
    }

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave) {
        this(player, new ChatMessage(quitMessage), autoSave);
    }

    public PlayerQuitEvent(Player player, Message quitMessage, boolean autoSave) {
        this(player, quitMessage, autoSave, "No reason");
    }

    public PlayerQuitEvent(Player player, Message quitMessage, boolean autoSave, String reason) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.autoSave = autoSave;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public Message getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(Message quitMessage) {
        this.quitMessage = quitMessage;
    }
}
