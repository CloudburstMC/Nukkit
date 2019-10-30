package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by on 15-10-13.
 */
@ToString
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    public int protocol;
    public UUID clientUUID;
    public long clientId;
    public Skin skin;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        if (protocol == 0) {
            setOffset(getOffset() + 2);
            this.protocol = getInt();
        }
        this.setBuffer(this.getByteArray(), 0);
        decodeChainData();
        decodeSkinData();
    }

    @Override
    public void encode() {

    }

    public int getProtocol() {
        return protocol;
    }

    private void decodeChainData() {
        Map<String, List<String>> map = new Gson().fromJson(new String(this.get(getLInt()), StandardCharsets.UTF_8),
                new TypeToken<Map<String, List<String>>>() {
                }.getType());
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");
        for (String c : chains) {
            JsonObject chainMap = decodeToken(c);
            if (chainMap == null) continue;
            if (chainMap.has("extraData")) {
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
            }
        }
    }

    private void decodeSkinData() {
        JsonObject skinToken = decodeToken(new String(this.get(this.getLInt())));
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        skin = new Skin();
        if (skinToken.has("SkinId")) {
            skin.setSkinId(skinToken.get("SkinId").getAsString());
        }
        if (skinToken.has("CapeId")) {
            skin.setSkinId(skinToken.get("CapeId").getAsString());
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

        if (skinToken.has("AnimationData")) {
            skin.setGeometryData(new String(Base64.getDecoder().decode(skinToken.get("AnimationData").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("AnimatedImageData")) {
            JsonArray array = skinToken.get("AnimatedImageData").getAsJsonArray();
            for (JsonElement element : array) {
                skin.getAnimations().add(getAnimation(element.getAsJsonObject()));
            }
        }
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject.class);
    }

    private static SkinAnimation getAnimation(JsonObject element) {
        float frames = element.get("Frames").getAsFloat();
        int type = element.get("Type").getAsInt();
        byte[] data = Base64.getDecoder().decode(element.get("Image").getAsString());
        int width = element.get("ImageWidth").getAsInt();
        int height = element.get("ImageHeight").getAsInt();
        return new SkinAnimation(new SerializedImage(width, height, data), type, frames);
    }

    private static SerializedImage getImage(JsonObject token, String name) {
        if (token.has(name + "Data") && token.has(name + "ImageHeight") && token.has(name + "ImageWidth")) {
            byte[] skinImage = Base64.getDecoder().decode(token.get(name + "Data").getAsString());
            int width = token.get(name + "ImageWidth").getAsInt();
            int height = token.get(name + "ImageHeight").getAsInt();
            return new SerializedImage(width, height, skinImage);
        }
        return null;
    }
}
