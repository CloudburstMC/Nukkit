package com.nukkitx.server.network.bedrock.session;

import com.nukkitx.api.Session;
import com.nukkitx.api.util.data.DeviceOS;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.network.bedrock.session.data.AuthData;
import com.nukkitx.server.network.bedrock.session.data.ClientData;
import lombok.Data;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@Data
public class LoginSession implements Session {
    private final BedrockServerSession session;
    private final NukkitServer server;
    private AuthData authData;
    private ClientData clientData;
    private int protocolVersion;

    public BedrockServerSession getBedrockSession() {
        return session;
    }

    @Nonnull
    @Override
    public NukkitServer getServer() {
        return server;
    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return Optional.of(session.getAddress());
    }

    @Override
    public boolean isXboxAuthenticated() {
        return authData.getXuid() != null;
    }

    @Nonnull
    @Override
    public Optional<String> getXuid() {
        return Optional.ofNullable(authData.getXuid());
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Nonnull
    @Override
    public String getName() {
        return authData.getDisplayName();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return authData.getIdentity();
    }

    @Nonnull
    @Override
    public DeviceOS getDeviceOS() {
        return clientData.getDeviceOs();
    }

    @Override
    public boolean isEducationEdition() {
        return this.clientData.isEduMode();
    }

    @Nonnull
    @Override
    public String getDeviceModel() {
        return this.clientData.getDeviceModel();
    }

    @Nonnull
    @Override
    public String getGameVersion() {
        return this.clientData.getGameVersion();
    }

    @Nonnull
    @Override
    public String getServerAddress() {
        return this.clientData.getServerAddress();
    }

    @Nonnull
    @Override
    public Optional<String> getActiveDirectoryRole() {
        return Optional.ofNullable(this.clientData.getActiveDirectoryRole());
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
