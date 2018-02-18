package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by on 15-10-13.
 */
public class LoginPacket extends DataPacket {

    public String username;
    public int protocol;
    public UUID clientUUID;
    public long clientId;

    public Skin skin;
    public String skinGeometryName;
    public byte[] skinGeometry;

    public byte[] capeData;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("LOGIN_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.protocol = this.getInt();
        if (protocol.getMainNumber() == 113) this.getByte();
        this.setBuffer(this.getByteArray(), 0);
        decodeChainData();
        decodeSkinData(protocol);
    }

    @Override
    public void encode(PlayerProtocol protocol) {

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

    private void decodeSkinData(PlayerProtocol protocol) {
        JsonObject skinToken = decodeToken(new String(this.get(this.getLInt())));
        String skinId = null;
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("SkinId")) skinId = skinToken.get("SkinId").getAsString();
        if (skinToken.has("SkinData")) {
            this.skin = new Skin(skinToken.get("SkinData").getAsString(), skinId);
            if (protocol.getMainNumber() >= 130) this.skin.setModel("Standard_"+this.skin.getModel().split("_")[1]);

            if (skinToken.has("CapeData"))
                this.skin.setCape(this.skin.new Cape(Base64.getDecoder().decode(skinToken.get("CapeData").getAsString())));
        }

        if (skinToken.has("SkinGeometryName")) this.skinGeometryName = skinToken.get("SkinGeometryName").getAsString();
        if (skinToken.has("SkinGeometry"))
            this.skinGeometry = Base64.getDecoder().decode(skinToken.get("SkinGeometry").getAsString());
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject.class);
    }

    @Override
    public Skin getSkin() {
        return this.skin;
    }
}
