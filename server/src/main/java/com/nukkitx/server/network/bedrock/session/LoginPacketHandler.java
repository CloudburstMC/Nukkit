package com.nukkitx.server.network.bedrock.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.JWSObject;
import com.nukkitx.api.event.player.PlayerPreLoginEvent;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.network.bedrock.session.data.AuthData;
import com.nukkitx.server.network.bedrock.session.data.ClientData;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.regex.Pattern;

@Log4j2
public class LoginPacketHandler implements BedrockPacketHandler {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]+$");

    private final BedrockServerSession session;
    private final NukkitServer server;
    private LoginSession loginSession;

    public LoginPacketHandler(BedrockServerSession session, NukkitServer server) {
        this.session = session;
        this.server = server;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        initializeResourcePackHandler();
        return true;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        int protocolVersion = packet.getProtocolVersion();

        if (protocolVersion != NukkitServer.MINECRAFT_CODEC.getProtocolVersion()) {
            PlayStatusPacket status = new PlayStatusPacket();
            if (protocolVersion > NukkitServer.MINECRAFT_CODEC.getProtocolVersion()) {
                status.setStatus(PlayStatusPacket.Status.FAILED_SERVER);
            } else {
                status.setStatus(PlayStatusPacket.Status.FAILED_CLIENT);
            }
            return true;
        }
        this.session.setPacketCodec(NukkitServer.MINECRAFT_CODEC);

        this.loginSession = new LoginSession(this.session, this.server);
        this.loginSession.setProtocolVersion(protocolVersion);

        JsonNode certData;
        try {
            certData = NukkitServer.JSON_MAPPER.readTree(packet.getChainData().toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Certificate JSON can not be read.");
        }

        JsonNode certChainData = certData.get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new RuntimeException("Certificate data is not valid");
        }

        boolean validChain;
        try {
            validChain = validateChainData(certChainData);

            JWSObject jwt = JWSObject.parse(certChainData.get(certChainData.size() - 1).asText());
            JsonNode payload = NukkitServer.JSON_MAPPER.readTree(jwt.getPayload().toBytes());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthDataImpl was not found!");
            }
            AuthData authData = NukkitServer.JSON_MAPPER.convertValue(payload.get("extraData"), AuthData.class);
            loginSession.setAuthData(authData);

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }

            if (!validChain) {
                // Disconnect if xbox auth is enabled.
                if (server.getConfiguration().getGeneral().isXboxAuthenticated()) {
                    session.disconnect("disconnectionScreen.notAuthenticated");
                    return true;
                }
                // Stop spoofing.
                authData.setXuid(null);
                // Check for valid name characters
                if (!USERNAME_PATTERN.matcher(authData.getDisplayName()).matches()) {
                    session.disconnect("disconnectionScreen.invalidName");
                    return true;
                }
            }

            ECPublicKey identityPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());

            if (!EncryptionUtils.verifyJwt(clientJwt, identityPublicKey) && server.getConfiguration().getGeneral().isXboxAuthenticated()) {
                session.disconnect("disconnectionScreen.invalidSkin");
            }

            JsonNode clientPayload = NukkitServer.JSON_MAPPER.readTree(clientJwt.getPayload().toBytes());
            ClientData clientData = NukkitServer.JSON_MAPPER.convertValue(clientPayload, ClientData.class);
            loginSession.setClientData(clientData);


            if (EncryptionUtils.canUseEncryption()) {
                startEncryptionHandshake(identityPublicKey);
            } else {
                initializeResourcePackHandler();
            }
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return true;
    }

    private void startEncryptionHandshake(PublicKey key) throws Exception {
        // Generate a fresh key for each session
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp384r1"));
        KeyPair serverKeyPair = generator.generateKeyPair();

        // Enable encryption server-side
        byte[] token = EncryptionUtils.generateRandomToken();
        SecretKey encryptionKey = EncryptionUtils.getSecretKey(serverKeyPair.getPrivate(), key, token);
        session.enableEncryption(encryptionKey);

        // Now send the packet to enable encryption on the client
        ServerToClientHandshakePacket packet = new ServerToClientHandshakePacket();
        packet.setJwt(EncryptionUtils.createHandshakeJwt(serverKeyPair, token).serialize());
        session.sendPacketImmediately(packet);
    }

    private void initializeResourcePackHandler() {
        LoginSession loginSession = new LoginSession(session, server);
        PlayerPreLoginEvent.Result result;
        if (loginSession.isBanned()) {
            result = PlayerPreLoginEvent.Result.BANNED;
        } else if (server.getConfiguration().getGeneral().isWhitelisted() && !loginSession.isWhitelisted()) {
            result = PlayerPreLoginEvent.Result.NOT_WHITELISTED;
        } else {
            result = null;
        }

        PlayerPreLoginEvent event = new PlayerPreLoginEvent(loginSession, result);
        server.getEventManager().fire(event);

        if (event.willDisconnect()) {
            String message;
            if (event.getDisconnectMessage() != null) {
                message = event.getDisconnectMessage();
            } else {
                message = event.getResult().getDisconnectMessage().i18n();
            }
            session.disconnect(message);
            return;
        }

        PlayStatusPacket status = new PlayStatusPacket();
        status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        session.sendPacket(status);

        ResourcePackPacketHandler handler = new ResourcePackPacketHandler(this.session, this.server, this.loginSession);
        session.setPacketHandler(handler);

        handler.sendResourcePacksInfo();
    }

    private boolean validateChainData(JsonNode data) throws Exception {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt = JWSObject.parse(node.asText());

            if (lastKey == null) {
                validChain = EncryptionUtils.verifyJwt(jwt, EncryptionUtils.getMojangPublicKey());
            } else {
                validChain = EncryptionUtils.verifyJwt(jwt, lastKey);
            }

            JsonNode payloadNode = NukkitServer.JSON_MAPPER.readTree(jwt.getPayload().toString());
            JsonNode ipkNode = payloadNode.get("identityPublicKey");
            Preconditions.checkState(ipkNode != null && ipkNode.getNodeType() == JsonNodeType.STRING, "identityPublicKey node is missing in chain");
            lastKey = EncryptionUtils.generateKey(ipkNode.asText());
        }
        return validChain;
    }
}
