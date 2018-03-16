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
        if (player.loggedIn)    {
            return;
        }

        String message;
        if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(this.getProtocol())) {
            if (this.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                message = "disconnectionScreen.outdatedClient";

                player.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT);
            } else {
                message = "disconnectionScreen.outdatedServer";

                player.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER);
            }
            if (this.protocol < 137) {
                DisconnectPacket disconnectPacket = new DisconnectPacket();
                disconnectPacket.message = message;
                disconnectPacket.encode();
                BatchPacket batch = new BatchPacket();
                batch.payload = disconnectPacket.getBuffer();
                player.directDataPacket(batch);
                // Still want to run close() to allow the player to be removed properly
            }
            player.close("", message, false);
            return;
        }

        player.username = TextFormat.clean(this.username);
        player.displayName = this.username;
        player.iusername = this.username.toLowerCase();
        player.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

        player.loginChainData = ClientChainData.read(this);

        if (!player.loginChainData.isXboxAuthed() && player.server.getPropertyBoolean("xbox-auth")) {
            player.kick(PlayerKickEvent.Reason.UNKNOWN, "disconnectionScreen.notAuthenticated", false);
        }

        if (player.server.getOnlinePlayers().size() >= player.server.getMaxPlayers() && player.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
            return;
        }

        player.randomClientId = this.clientId;

        player.uuid = this.clientUUID;
        player.rawUUID = Binary.writeUUID(player.uuid);

        boolean valid = true;
        int len = this.username.length();
        if (len > 16 || len < 3) {
            valid = false;
        }

        for (int i = 0; i < len && valid; i++) {
            char c = this.username.charAt(i);
            if ((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    c == '_' || c == ' '
                    ) {
                continue;
            }

            valid = false;
            break;
        }

        if (!valid || Objects.equals(player.iusername, "rcon") || Objects.equals(player.iusername, "console")) {
            player.close("", "disconnectionScreen.invalidName");

            return;
        }

        if (!this.skin.isValid()) {
            player.close("", "disconnectionScreen.invalidSkin");
            return;
        } else {
            player.setSkin(this.getSkin());
        }

        PlayerPreLoginEvent playerPreLoginEvent;
        player.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(player, "Plugin reason"));
        if (playerPreLoginEvent.isCancelled()) {
            player.close("", playerPreLoginEvent.getKickMessage());

            return;
        }

        player.preLoginEventTask = new AsyncTask() {

            private PlayerAsyncPreLoginEvent e;

            @Override
            public void onRun() {
                e = new PlayerAsyncPreLoginEvent(player.username, player.uuid, player.ip, player.port);
                player.server.getPluginManager().callEvent(e);
            }

            @Override
            public void onCompletion(Server server) {
                if (!player.closed) {
                    if (e.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                        player.close(e.getKickMessage(), e.getKickMessage());
                    } else if (player.shouldLogin) {
                        player.completeLoginSequence();
                    }
                }
            }
        };
        player.server.getScheduler().scheduleAsyncTask(player.preLoginEventTask);

        player.processLogin();
    }
}
