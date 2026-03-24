package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.auth.AuthPayload;
import cn.nukkit.network.auth.AuthType;
import cn.nukkit.network.auth.CertificateChainPayload;
import cn.nukkit.network.auth.TokenPayload;
import cn.nukkit.network.encryption.EncryptionUtils;
import cn.nukkit.network.encryption.util.ChainValidationResult;
import cn.nukkit.network.protocol.LoginPacket;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jose4j.json.JsonUtil;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ClientChainData is a container of chain data sent from clients.
 * <p>
 * Device information such as client UUID, xuid and serverAddress, can be
 * read from instances of this object.
 * <p>
 * To get chain data, you can use player.getLoginChainData() or read(loginPacket)
 * <p>
 * ===============
 * @author boybook
 * Nukkit Project
 * ===============
 */
public final class ClientChainData implements LoginChainData {

    private static final Gson GSON = new Gson();

    public static ClientChainData of(byte[] buffer) {
        return new ClientChainData(buffer);
    }

    public static ClientChainData read(LoginPacket pk) {
        return of(pk.getBuffer());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getClientUUID() {
        return clientUUID;
    }

    @Override
    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    @Override
    public long getClientId() {
        return clientId;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public String getDeviceModel() {
        return deviceModel;
    }

    @Override
    public int getDeviceOS() {
        return deviceOS;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public int getGuiScale() {
        return guiScale;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public String getXUID() {
        return xuid;
    }

    private boolean xboxAuthed;

    @Override
    public int getCurrentInputMode() {
        return currentInputMode;
    }

    @Override
    public int getDefaultInputMode() {
        return defaultInputMode;
    }

    @Override
    public String getCapeData() {
        return capeData;
    }

    public final static int UI_PROFILE_CLASSIC = 0;
    public final static int UI_PROFILE_POCKET = 1;

    @Override
    public int getUIProfile() {
        return UIProfile;
    }

    @Override
    public String getTitleId() {
        return titleId;
    }

    @Override
    public JsonObject getRawData() {
        return rawData;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Override
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientChainData && Objects.equals(bs, ((ClientChainData) obj).bs);
    }

    @Override
    public int hashCode() {
        return bs.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    private String username;
    private UUID clientUUID;
    private String xuid;
    private String identityPublicKey;

    private long clientId;
    private String serverAddress;
    private String deviceModel;
    private int deviceOS;
    private String deviceId;
    private String gameVersion;
    private int guiScale;
    private String languageCode;
    private int currentInputMode;
    private int defaultInputMode;
    private int UIProfile;
    private String capeData;
    private String titleId;
    @Getter
    private Skin skin;

    private JsonObject rawData;

    private final BinaryStream bs = new BinaryStream();

    private ClientChainData(byte[] buffer) {
        bs.setBuffer(buffer, 0);
        decodeChainData();
        decodeSkinData();
    }

    @Override
    public boolean isXboxAuthed() {
        return xboxAuthed;
    }

    private void decodeSkinData() {
        int size = bs.getLInt();
        if (size > 52428800) {
            throw new TooBigSkinException("The skin data is too big: " + size);
        }

        String valueJwt = new String(bs.get(size), StandardCharsets.UTF_8);

        try {
            if (this.xboxAuthed && EncryptionUtils.verifyClientData(valueJwt, this.identityPublicKey) == null) {
                throw new IllegalStateException("Client data isn't signed by the given chain data");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Client data isn't signed by the given chain data");
        }

        JsonObject skinToken = decodeToken(valueJwt);
        if (skinToken == null) throw new RuntimeException("Invalid null skin token");
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("ServerAddress")) this.serverAddress = skinToken.get("ServerAddress").getAsString();
        if (skinToken.has("DeviceModel")) this.deviceModel = skinToken.get("DeviceModel").getAsString();
        if (skinToken.has("DeviceOS")) this.deviceOS = skinToken.get("DeviceOS").getAsInt();
        if (skinToken.has("DeviceId")) this.deviceId = skinToken.get("DeviceId").getAsString();
        if (skinToken.has("GameVersion")) this.gameVersion = skinToken.get("GameVersion").getAsString();
        if (skinToken.has("GuiScale")) this.guiScale = skinToken.get("GuiScale").getAsInt();
        if (skinToken.has("LanguageCode")) this.languageCode = skinToken.get("LanguageCode").getAsString();
        if (skinToken.has("CurrentInputMode")) this.currentInputMode = skinToken.get("CurrentInputMode").getAsInt();
        if (skinToken.has("DefaultInputMode")) this.defaultInputMode = skinToken.get("DefaultInputMode").getAsInt();
        if (skinToken.has("UIProfile")) this.UIProfile = skinToken.get("UIProfile").getAsInt();
        if (skinToken.has("CapeData")) this.capeData = skinToken.get("CapeData").getAsString();
        this.skin = decodeSkin(skinToken);
        this.rawData = skinToken;
    }

    public static JsonObject decodeToken(String token) {
        String[] base = token.split("\\.", 5);
        if (base.length < 2) return null;
        byte[] decoded;
        try {
            decoded = Base64.getUrlDecoder().decode(base[1]);
        } catch (IllegalArgumentException ex) {
            Server.getInstance().getLogger().error("Unable to decode token: " + token, ex);
            decoded = Base64.getDecoder().decode(base[1]);
        }
        return GSON.fromJson(new String(decoded, StandardCharsets.UTF_8), JsonObject.class);
    }

    private AuthPayload readAuthJwt(String authJwt) {
        try {
            Map<String, Object> payload = JsonUtil.parseJson(authJwt);
            if (!payload.containsKey("AuthenticationType")) {
                throw new IllegalArgumentException("Missing AuthenticationType in JWT");
            }

            int authTypeOrdinal = ((Number) payload.get("AuthenticationType")).intValue();
            if (authTypeOrdinal < 0 || authTypeOrdinal >= AuthType.values().length - 1) {
                throw new IllegalArgumentException("Invalid AuthenticationType ordinal: " + authTypeOrdinal);
            }
            AuthType authType = AuthType.values()[authTypeOrdinal + 1];

            if (payload.containsKey("Token") && payload.get("Token") instanceof String && !((String) payload.get("Token")).isEmpty()) {
                String token = (String) payload.get("Token");
                return new TokenPayload(token, authType);
            } else if (payload.containsKey("Certificate") && payload.get("Certificate") instanceof String && !((String) payload.get("Certificate")).isEmpty()) {
                String certJson = (String) payload.get("Certificate");
                Map<String, Object> certData = JsonUtil.parseJson(certJson);
                if (!certData.containsKey("chain") || !(certData.get("chain") instanceof List)) {
                    throw new IllegalArgumentException("Invalid Certificate chain in JWT");
                }
                List<String> chain = (List<String>) certData.get("chain");
                return new CertificateChainPayload(chain, authType);
            } else {
                throw new IllegalArgumentException("Invalid AuthPayload in JWT");
            }
        } catch (JoseException e) {
            throw new IllegalArgumentException("Failed to parse auth payload", e);
        }
    }

    private void decodeChainData() {
        int size = bs.getLInt();
        if (size > 3145728) {
            throw new IllegalArgumentException("The chain data is too big: " + size);
        }

        String authJwt = new String(bs.get(size), StandardCharsets.US_ASCII);

        ChainValidationResult result;
        try {
            result = EncryptionUtils.validatePayload(readAuthJwt(authJwt));

            this.xboxAuthed = result.signed();
        } catch (Throwable e) {
            this.xboxAuthed = false;
            Server.getInstance().getLogger().logException(e);
            return;
        }

        ChainValidationResult.IdentityData extraData = result.identityClaims().extraData;
        this.username = extraData.displayName;
        this.clientUUID = extraData.identity;
        this.xuid = extraData.xuid;
        this.titleId = extraData.titleId;
        this.identityPublicKey = result.identityClaims().identityPublicKey;
    }

    private Skin decodeSkin(JsonObject skinToken) {
        Skin skin = new Skin();
        skin.setTrusted(false); // Don't trust player skins

        if (skinToken.has("SkinId")) {
            skin.setSkinId(skinToken.get("SkinId").getAsString());
        } else {
            skin.setSkinId(UUID.randomUUID().toString());
        }

        skin.setFullSkinId(skin.getSkinId());

        if (skinToken.has("PlayFabId")) {
            skin.setPlayFabId(skinToken.get("PlayFabId").getAsString());
        }

        if (skinToken.has("CapeId")) {
            skin.setCapeId(skinToken.get("CapeId").getAsString());
        }

        skin.setSkinData(getImage(skinToken, "Skin"));
        skin.setCapeData(getImage(skinToken, "Cape"));

        if (skinToken.has("PremiumSkin")) {
            skin.setPremium(skinToken.get("PremiumSkin").getAsBoolean());
        }

        if (skinToken.has("PersonaSkin")) {
            skin.setPersona(skinToken.get("PersonaSkin").getAsBoolean());
        }

        if (skinToken.has("CapeOnClassicSkin")) {
            skin.setCapeOnClassic(skinToken.get("CapeOnClassicSkin").getAsBoolean());
        }

        if (skinToken.has("SkinResourcePatch")) {
            skin.setSkinResourcePatch(new String(Base64.getDecoder().decode(skinToken.get("SkinResourcePatch").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinGeometryData")) {
            skin.setGeometryData(new String(Base64.getDecoder().decode(skinToken.get("SkinGeometryData").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinAnimationData")) {
            skin.setAnimationData(new String(Base64.getDecoder().decode(skinToken.get("SkinAnimationData").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("AnimatedImageData")) {
            for (JsonElement element : skinToken.get("AnimatedImageData").getAsJsonArray()) {
                skin.getAnimations().add(getAnimation(element.getAsJsonObject()));
            }
        }

        if (skinToken.has("SkinColor")) {
            skin.setSkinColor(skinToken.get("SkinColor").getAsString());
        }

        if (skinToken.has("ArmSize")) {
            skin.setArmSize(skinToken.get("ArmSize").getAsString());
        }

        if (skinToken.has("PersonaPieces")) {
            for (JsonElement object : skinToken.get("PersonaPieces").getAsJsonArray()) {
                skin.getPersonaPieces().add(getPersonaPiece(object.getAsJsonObject()));
            }
        }

        if (skinToken.has("PieceTintColors")) {
            for (JsonElement object : skinToken.get("PieceTintColors").getAsJsonArray()) {
                skin.getTintColors().add(getTint(object.getAsJsonObject()));
            }
        }

        return skin;
    }

    private static SkinAnimation getAnimation(JsonObject element) {
        float frames = element.get("Frames").getAsFloat();
        int type = element.get("Type").getAsInt();
        byte[] data = Base64.getDecoder().decode(element.get("Image").getAsString());
        int width = element.get("ImageWidth").getAsInt();
        int height = element.get("ImageHeight").getAsInt();
        int expression = element.get("AnimationExpression").getAsInt();
        return new SkinAnimation(new SerializedImage(width, height, data), type, frames, expression);
    }

    private static SerializedImage getImage(JsonObject token, String name) {
        if (token.has(name + "Data")) {
            byte[] skinImage = Base64.getDecoder().decode(token.get(name + "Data").getAsString());
            if (token.has(name + "ImageHeight") && token.has(name + "ImageWidth")) {
                int width = token.get(name + "ImageWidth").getAsInt();
                int height = token.get(name + "ImageHeight").getAsInt();
                return new SerializedImage(width, height, skinImage);
            } else {
                return SerializedImage.fromLegacy(skinImage);
            }
        }
        return SerializedImage.EMPTY;
    }

    private static PersonaPiece getPersonaPiece(JsonObject object) {
        String pieceId = object.get("PieceId").getAsString();
        String pieceType = object.get("PieceType").getAsString();
        String packId = object.get("PackId").getAsString();
        boolean isDefault = object.get("IsDefault").getAsBoolean();
        String productId = object.get("ProductId").getAsString();
        return new PersonaPiece(pieceId, pieceType, packId, isDefault, productId);
    }

    public static PersonaPieceTint getTint(JsonObject object) {
        String pieceType = object.get("PieceType").getAsString();
        List<String> colors = new ArrayList<>();
        for (JsonElement element : object.get("Colors").getAsJsonArray()) {
            colors.add(element.getAsString()); // remove #
        }
        return new PersonaPieceTint(pieceType, colors);
    }

    public static class TooBigSkinException extends RuntimeException {

        public TooBigSkinException(String s) {
            super(s);
        }
    }
}
