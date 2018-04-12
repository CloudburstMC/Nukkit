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

import cn.nukkit.api.event.player.PlayerPreLoginEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.minecraft.MinecraftPacketRegistry;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.packet.ClientToServerHandshakePacket;
import cn.nukkit.server.network.minecraft.packet.LoginPacket;
import cn.nukkit.server.network.minecraft.packet.PlayStatusPacket;
import cn.nukkit.server.network.minecraft.packet.ResourcePacksInfoPacket;
import cn.nukkit.server.network.minecraft.session.data.AuthData;
import cn.nukkit.server.network.minecraft.session.data.ClientData;
import cn.nukkit.server.network.util.EncryptionUtil;
import cn.nukkit.server.util.NativeCodeFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.voxelwind.server.jni.CryptoUtil;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

@Log4j2
public class LoginPacketHandler implements NetworkPacketHandler {
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

    private final MinecraftSession session;

    public LoginPacketHandler(MinecraftSession session) {
        this.session = session;
    }

    private static ECPublicKey generateKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }

    @Override
    public void handle(ClientToServerHandshakePacket packet) {
        initializePlayerSession();
    }

    @Override
    public void handle(LoginPacket packet) {
        session.setProtocolVersion(packet.getProtocolVersion());
        if (packet.getChainData() == null) {
            // Client has incompatible version.
            PlayStatusPacket status = new PlayStatusPacket();
            if (packet.getProtocolVersion() > MinecraftPacketRegistry.BROADCAST_PROTOCOL_VERSION) {
                session.disconnect("disconnectionScreen.outdatedServer");
            } else {
                session.disconnect("disconnectionScreen.outdatedClient");
            }
            return;
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
                throw new RuntimeException("AuthData was not found!");
            }
            AuthData authData = NukkitServer.JSON_MAPPER.convertValue(payload.get("extraData"), AuthData.class);
            session.setAuthData(authData);

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());

            verifyJwt(clientJwt, identityPublicKey);
            JsonNode clientPayload = NukkitServer.JSON_MAPPER.readTree(clientJwt.getPayload().toBytes());
            ClientData clientData = NukkitServer.JSON_MAPPER.convertValue(clientPayload, ClientData.class);
            session.setClientData(clientData);

            if (!validChain && session.getServer().getConfiguration().getGeneral().isXboxAuthenticated()) {
                session.disconnect("disconnectionScreen.notAuthenticated");
                return;
            }

            if (!validChain) {
                // Stop spoofing.
                session.getAuthData().setXuid(null);
                // Check for valid name characters
                if (USERNAME_PATTERN.matcher(authData.getDisplayName()).find()) {
                    session.disconnect("disconnectionScreen.invalidName");
                }
                // Use server side UUID.
                session.getAuthData().setOfflineIdentity(UUID.nameUUIDFromBytes(authData.getDisplayName().toLowerCase().getBytes(StandardCharsets.UTF_8)));
            }

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
        session.addToSendQueue(status);

        PlayerSession playerSession = session.initializePlayerSession(session.getServer().getDefaultLevel());
        session.setHandler(playerSession.getNetworkPacketHandler());

        ResourcePacksInfoPacket info = new ResourcePacksInfoPacket();
        session.addToSendQueue(info);
    }

    private boolean validateChainData(JsonNode data) throws Exception {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt = JWSObject.parse(node.asText());

            if (!validChain) {
                validChain = verifyJwt(jwt, MOJANG_PUBLIC_KEY);
            }

            if (lastKey != null) {
                verifyJwt(jwt, lastKey);
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
