package com.nukkitx.server.network.bedrock.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nukkitx.api.event.player.PlayerPreLoginEvent;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.network.bedrock.packet.ResourcePacksInfoPacket;
import com.nukkitx.server.network.bedrock.session.data.AuthDataImpl;
import com.nukkitx.server.network.bedrock.session.data.ClientDataImpl;
import com.nukkitx.server.network.util.EncryptionUtil;
import com.nukkitx.server.util.NativeCodeFactory;
import com.voxelwind.server.jni.CryptoUtil;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

@Log4j2
public class LoginPacketHandler implements BedrockPacketHandler {
    private static final boolean CAN_USE_ENCRYPTION = CryptoUtil.isJCEUnlimitedStrength() || NativeCodeFactory.cipher.isLoaded();
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static final ECPublicKey MOJANG_PUBLIC_KEY;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]*$");

    static {
        try {
            MOJANG_PUBLIC_KEY = generateKey(MOJANG_PUBLIC_KEY_BASE64);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private final BedrockSession<NukkitPlayerSession> session;
    private final NukkitServer server;

    public LoginPacketHandler(BedrockSession<NukkitPlayerSession> session, NukkitServer server) {
        this.session = session;
        this.server = server;
    }

    private static ECPublicKey generateKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        initializePlayerSession();
        return true;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        int protocolVersion = packet.getProtocolVersion();
        session.setProtocolVersion(protocolVersion);

        if (!session.getPacketCodec().isCompatibleVersion(protocolVersion)) {
            PlayStatusPacket status = new PlayStatusPacket();
            if (protocolVersion > BedrockPacketCodec.BROADCAST_PROTOCOL_VERSION) {
                status.setStatus(PlayStatusPacket.Status.FAILED_SERVER);
            } else {
                status.setStatus(PlayStatusPacket.Status.FAILED_CLIENT);
            }
        }

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
            AuthDataImpl authData = NukkitServer.JSON_MAPPER.convertValue(payload.get("extraData"), AuthDataImpl.class);
            session.setAuthData(authData);

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

            ECPublicKey identityPublicKey = generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());

            if (!verifyJwt(clientJwt, identityPublicKey) && server.getConfiguration().getGeneral().isXboxAuthenticated()) {
                session.disconnect("disconnectionScreen.invalidSkin");
            }

            JsonNode clientPayload = NukkitServer.JSON_MAPPER.readTree(clientJwt.getPayload().toBytes());
            ClientDataImpl clientData = NukkitServer.JSON_MAPPER.convertValue(clientPayload, ClientDataImpl.class);
            session.setClientData(clientData);


            if (CAN_USE_ENCRYPTION) {
                startEncryptionHandshake(identityPublicKey);
            } else {
                initializePlayerSession();
            }
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
    }

    private void startEncryptionHandshake(PublicKey key) throws Exception {
        // Generate a fresh key for each session
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp384r1"));
        KeyPair serverKeyPair = generator.generateKeyPair();

        // Enable encryption server-side
        byte[] token = EncryptionUtil.generateRandomToken();
        byte[] serverKey = EncryptionUtil.getServerKey(serverKeyPair, key, token);
        session.enableEncryption(serverKey);

        // Now send the packet to enable encryption on the client
        session.sendImmediatePackage(EncryptionUtil.createHandshakePacket(serverKeyPair, token));
    }

    private void initializePlayerSession() {
        LoginSession loginSession = new LoginSession(session);
        PlayerPreLoginEvent.Result result;
        if (loginSession.isBanned()) {
            result = PlayerPreLoginEvent.Result.BANNED;
        } else if (session.getServer().getConfiguration().getGeneral().isWhitelisted() && !loginSession.isWhitelisted()) {
            result = PlayerPreLoginEvent.Result.NOT_WHITELISTED;
        } else {
            result = null;
        }

        PlayerPreLoginEvent event = new PlayerPreLoginEvent(loginSession, result);
        session.getServer().getEventManager().fire(event);

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

        NukkitPlayerSession playerSession = session.initializePlayerSession(session.getServer().getDefaultLevel());
        session.setHandler(playerSession.getNetworkPacketHandler());

        ResourcePacksInfoPacket info = new ResourcePacksInfoPacket();
        session.sendPacket(info);
    }

    private boolean validateChainData(JsonNode data) throws Exception {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt = JWSObject.parse(node.asText());

            if (lastKey == null) {
                validChain = verifyJwt(jwt, MOJANG_PUBLIC_KEY);
            } else {
                validChain = verifyJwt(jwt, lastKey);
            }

            JsonNode payloadNode = NukkitServer.JSON_MAPPER.readTree(jwt.getPayload().toString());
            JsonNode ipkNode = payloadNode.get("identityPublicKey");
            Preconditions.checkState(ipkNode != null && ipkNode.getNodeType() == JsonNodeType.STRING, "identityPublicKey node is missing in chain");
            lastKey = generateKey(ipkNode.asText());
        }
        return validChain;
    }

    private boolean verifyJwt(JWSObject jwt, ECPublicKey key) throws JOSEException {
        return jwt.verify(new DefaultJWSVerifierFactory().createJWSVerifier(jwt.getHeader(), key));
    }
}
