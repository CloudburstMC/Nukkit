package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.*;

@ToString
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    private int protocol_;
    public UUID clientUUID;
    public long clientId;
    public Skin skin;

    private static final Gson GSON = new Gson();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol_ = this.getInt();
        if (this.protocol_ == 0) {
            setOffset(getOffset() + 2);
            this.protocol_ = getInt();
        }
        if (ProtocolInfo.SUPPORTED_PROTOCOLS.contains(this.protocol_)) { // Avoid errors with unsupported versions
            this.setBuffer(this.getByteArray(), 0);
            decodeChainData();
            decodeSkinData();
        }
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    public int getProtocol() {
        return protocol_;
    }

    private void decodeChainData() {
        int size = this.getLInt();
        if (size > 3145728) {
            throw new IllegalArgumentException("The chain data is too big: " + size);
        }

        String data = new String(this.get(size), StandardCharsets.UTF_8);

        Map<String, Object> map = GSON.fromJson(data, new MapTypeToken());

        String certificate = (String) map.get("Certificate");
        if (certificate != null) {
            map = GSON.fromJson(certificate, new MapTypeToken());
        }

        List<String> chains = (List<String>) map.get("chain");
        if (chains == null || chains.isEmpty()) {
            return;
        }

        for (String c : chains) {
            JsonObject chainMap = ClientChainData.decodeToken(c);
            if (chainMap == null) continue;

            if (chainMap.has("extraData")) {
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
            }
        }
    }

    private void decodeSkinData() {
        int size = this.getLInt();
        if (size > 52428800) {
            Server.getInstance().getLogger().warning(username + ": The skin data is too big: " + size);
            return; // Get disconnected due to "invalid skin"
        }

        JsonObject skinToken = ClientChainData.decodeToken(new String(this.get(size), StandardCharsets.UTF_8));
        if (skinToken == null) throw new RuntimeException("Invalid null skin token");

        if (skinToken.has("ClientRandomId")) {
            this.clientId = skinToken.get("ClientRandomId").getAsLong();
        }

        skin = new Skin();
        skin.setTrusted(false); // Don't trust player skins

        if (skinToken.has("SkinId")) {
            skin.setSkinId(skinToken.get("SkinId").getAsString());
        } else {
            skin.setSkinId(UUID.randomUUID().toString());
        }

        skin.setFullSkinId(skin.getSkinId());

        if (protocol_ < 388) {
            if (skinToken.has("SkinData")) {
                skin.setSkinData(Base64.getDecoder().decode(skinToken.get("SkinData").getAsString()));
            }

            if (skinToken.has("CapeData")) {
                skin.setCapeData(Base64.getDecoder().decode(skinToken.get("CapeData").getAsString()));
            }

            if (skinToken.has("SkinGeometryName")) {
                skin.setGeometryName(skinToken.get("SkinGeometryName").getAsString());
            }

            if (skinToken.has("SkinGeometry")) {
                skin.setGeometryData(new String(Base64.getDecoder().decode(skinToken.get("SkinGeometry").getAsString()), StandardCharsets.UTF_8));
            }
        } else {
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
        }
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

    private static class MapTypeToken extends TypeToken<Map<String, Object>> {
    }
}