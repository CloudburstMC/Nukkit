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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PlayerJoinEvent implements PlayerEvent {
    private final Player player;
    private Message message;

    public PlayerJoinEvent(Player player, String message) {
        this(player, new ChatMessage(message));
    }

    public PlayerJoinEvent(Player player, Message message) {
        this.player = player;
        this.message = message;
    }

    public boolean isSilentlyJoining() {
        return message == null;
    }

    public void silentlyJoin() {
        message = null;
    }

    @Nonnull
    public Optional<Message> getJoinMessage() {
        return Optional.ofNullable(message);
    }

    public void setJoinMessage(@Nullable Message message) {
        this.message = message;
    }

    public void setJoinMessage(@Nullable String message) {
        if (message == null) {
            this.message = null;
        } else {
            this.setJoinMessage(new ChatMessage(message));
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
