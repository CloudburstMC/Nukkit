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
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.message.ChatMessage;
import cn.nukkit.api.message.Message;

public class PlayerKickEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Reason reason;
    private final String reasonString;
    private Message quitMessage;
    private boolean cancelled;

    public PlayerKickEvent(Player player, Reason reason, String quitMessage) {
        this(player, reason, new ChatMessage(quitMessage));
    }

    public PlayerKickEvent(Player player, Reason reason, Message quitMessage) {
        this(player, reason, reason.toString(), quitMessage);
    }

    public PlayerKickEvent(Player player, Reason reason, String reasonString, Message quitMessage) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.reason = reason;
        this.reasonString = reason.name();
    }

    public Reason getReason() {
        return reason;
    }

    public Message getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(Message quitMessage) {
        this.quitMessage = quitMessage;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public String getReasonString() {
        return reasonString;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Reason {
        NEW_CONNECTION,
        KICKED_BY_ADMIN,
        NOT_WHITELISTED,
        IP_BANNED,
        NAME_BANNED,
        INVALID_PVE,
        LOGIN_TIMEOUT,
        SERVER_FULL,
        FLYING_DISABLED,
        UNKNOWN;

        @Override
        public String toString() {
            return this.name();
        }
    }
}
