package cn.nukkit.network.encryption.util;

import cn.nukkit.network.encryption.EncryptionUtils;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static cn.nukkit.network.encryption.util.JsonUtils.childAsType;

@ToString
public final class ChainValidationResult {

    private final boolean signed;
    private final Map<String, Object> parsedPayload;
    private final JwtContext jwtContext;

    private IdentityClaims identityClaims;

    public ChainValidationResult(boolean signed, String rawPayload) throws JoseException {
        this(signed, JsonUtil.parseJson(rawPayload));
    }

    public ChainValidationResult(boolean signed, Map<String, Object> parsedPayload) {
        this.signed = signed;
        this.parsedPayload = Objects.requireNonNull(parsedPayload);
        this.jwtContext = null;
    }

    public ChainValidationResult(boolean signed, JwtContext context) {
        this.signed = signed;
        this.jwtContext = Objects.requireNonNull(context);
        this.parsedPayload = null;
    }

    public boolean signed() {
        return signed;
    }

    public Map<String, Object> rawIdentityClaims() {
        if (parsedPayload == null) {
            return jwtContext.getJwtClaims().getClaimsMap();
        } else {
            return new HashMap<>(parsedPayload);
        }
    }

    public IdentityClaims identityClaims() throws IllegalStateException {
        if (identityClaims == null) {
            if (parsedPayload == null) {
                identityClaims = createClaims();
            } else {
                identityClaims = createLegacyClaims();
            }
        }
        return identityClaims;
    }

    private IdentityClaims createLegacyClaims() {
        String identityPublicKey = childAsType(parsedPayload, "identityPublicKey", String.class);
        Map<?, ?> extraData = childAsType(parsedPayload, "extraData", Map.class);

        String displayName = childAsType(extraData, "displayName", String.class);
        String identityString = childAsType(extraData, "identity", String.class);
        String xuid = childAsType(extraData, "XUID", String.class);
        Object titleId = extraData.get("titleId");

        UUID identity;
        try {
            identity = UUID.fromString(identityString);
        } catch (Exception exception) {
            throw new IllegalStateException("identity node is an invalid UUID");
        }

        return new IdentityClaims(
                new IdentityData(displayName, identity, xuid, (String) titleId, null),
                identityPublicKey
        );
    }

    private IdentityClaims createClaims() {
        JwtClaims claims = jwtContext.getJwtClaims();

        String identityPublicKey = claims.getClaimValueAsString("cpk");
        String displayName = claims.getClaimValueAsString("xname");
        String xuid = claims.getClaimValueAsString("xid");
        String minecraftId = claims.getClaimValueAsString("mid");
        UUID identity = UUID.nameUUIDFromBytes(("pocket-auth-1-xuid:" + xuid).getBytes(StandardCharsets.UTF_8));

        return new IdentityClaims(
                new IdentityData(displayName, identity, xuid, null, minecraftId),
                identityPublicKey
        );
    }

    @ToString
    public static final class IdentityClaims {

        public final IdentityData extraData;
        public final String identityPublicKey;
        private PublicKey parsedIdentityPublicKey;

        private IdentityClaims(IdentityData extraData, String identityPublicKey) {
            this.extraData = extraData;
            this.identityPublicKey = identityPublicKey;
        }

        public PublicKey parsedIdentityPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
            if (parsedIdentityPublicKey == null) {
                parsedIdentityPublicKey = EncryptionUtils.parseKey(identityPublicKey);
            }
            return parsedIdentityPublicKey;
        }
    }

    @ToString
    public static final class IdentityData {

        public final String displayName;
        /**
         * Identity UUID, derived from the XUID when online, or from the username when offline.
         * @deprecated v818: Use {@link #minecraftId} instead.
         */
        @Nullable
        public final UUID identity;
        public final String xuid;
        public final @Nullable String titleId;
        /**
         * The player's Minecraft PlayFab ID
         * @since v818
         */
        @Nullable
        public final String minecraftId;

        private IdentityData(String displayName, UUID identity, String xuid, @Nullable String titleId, @Nullable String minecraftId) {
            this.displayName = displayName;
            this.identity = identity;
            this.xuid = xuid;
            this.titleId = titleId;
            this.minecraftId = minecraftId;
        }
    }
}