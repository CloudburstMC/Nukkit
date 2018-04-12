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

package cn.nukkit.server.network.minecraft.session;

import cn.nukkit.api.Session;
import cn.nukkit.api.util.data.DeviceOS;
import cn.nukkit.server.NukkitServer;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class LoginSession implements Session {
    private final MinecraftSession session;

    public LoginSession(MinecraftSession session) {
        this.session = session;
    }

    public MinecraftSession getSession() {
        return session;
    }

    @Nonnull
    @Override
    public NukkitServer getServer() {
        return session.getServer();
    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Override
    public boolean isXboxAuthenticated() {
        return session.getAuthData().getXuid() != null;
    }

    @Nonnull
    @Override
    public Optional<String> getXuid() {
        return Optional.ofNullable(session.getAuthData().getXuid());
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Nonnull
    @Override
    public String getName() {
        return session.getAuthData().getDisplayName();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return session.getAuthData().getIdentity();
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return session.getClientData().getDeviceOs();
    }

    @Override
    public boolean isEducationEdition() {
        return session.getClientData().isEducationMode();
    }

    @Nonnull
    @Override
    public String getDeviceModel() {
        return session.getClientData().getDeviceModel();
    }

    @Nonnull
    @Override
    public String getGameVersion() {
        return session.getClientData().getGameVersion();
    }

    @Nonnull
    @Override
    public String getServerAddress() {
        return session.getClientData().getServerAddress();
    }

    @Nonnull
    @Override
    public Optional<String> getActiveDirectoryRole() {
        return Optional.ofNullable(session.getClientData().getActiveDirectoryRole());
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean value) {

    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean value) {

    }
}
