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

import cn.nukkit.api.Session;
import cn.nukkit.api.event.Event;
import cn.nukkit.api.util.data.DisconnectMessage;

import javax.annotation.Nullable;

public class PlayerPreLoginEvent implements Event {
    private final Session session;
    private Result result;
    private String disconnectMessage = null;

    public PlayerPreLoginEvent(Session session, Result result) {
        this.session = session;
        this.result = result;
    }

    public boolean willDisconnect() {
        return result != null;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }

    public void setDisconnectMessage(String disconnectMessage) {
        this.disconnectMessage = disconnectMessage;
    }

    @Nullable
    public Result getResult() {
        return result;
    }

    public void setResult(@Nullable Result result) {
        this.result = result;
    }

    public Session getSession() {
        return session;
    }

    public enum Result {
        DISCONNECTED(DisconnectMessage.NO_REASON),
        BANNED(DisconnectMessage.KICKED),
        NOT_WHITELISTED(DisconnectMessage.NOT_ALLOWED);

        private final DisconnectMessage message;

        Result(DisconnectMessage message) {
            this.message = message;
        }

        public DisconnectMessage getDisconnectMessage() {
            return message;
        }
    }
}
