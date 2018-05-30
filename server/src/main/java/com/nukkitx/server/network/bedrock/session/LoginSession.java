package com.nukkitx.server.network.bedrock.session;

import com.nukkitx.api.Session;
import com.nukkitx.api.util.data.DeviceOS;
import com.nukkitx.server.NukkitServer;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class LoginSession implements Session {
    private final BedrockSession session;

    public LoginSession(BedrockSession session) {
        this.session = session;
    }

    public BedrockSession getSession() {
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
