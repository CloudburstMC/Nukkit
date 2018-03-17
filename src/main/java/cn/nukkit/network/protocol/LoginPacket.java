package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static cn.nukkit.entity.Entity.DATA_NAMETAG;


/**
 * Created by on 15-10-13.
 */
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    public int protocol;
    public UUID clientUUID;
    public long clientId;

    public Skin skin;
    public String skinGeometryName;
    public byte[] skinGeometry;

    public byte[] capeData;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        if (protocol >= 0xffff) {
            this.offset -= 6;
            this.protocol = this.getInt();
            this.offset += 1;
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
        String skinId = null;
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("SkinId")) skinId = skinToken.get("SkinId").getAsString();
        if (skinToken.has("SkinData")) {
            this.skin = new Skin(skinToken.get("SkinData").getAsString(), skinId);

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

    @Override
    public void handle(Player player) {
        player.handle(this);
    }
}
