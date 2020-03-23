package cn.nukkit.player.handler;

import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.ProtocolInfo;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.TextFormat;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Extollite
 */
public class PlayerPacketHandler implements BedrockPacketHandler {
    private final Player player;

    public PlayerPacketHandler(Player player) {
        this.player = player;
    }

    @Override
    public boolean handle(LoginPacket packet){
        if (player.loggedIn) {
            return true;
        }

        int protocolVersion = packet.getProtocolVersion();
        BedrockPacketCodec packetCodec = ProtocolInfo.getPacketCodec(protocolVersion);

        if (packetCodec == null) {
            String message;
            if (protocolVersion < ProtocolInfo.getDefaultProtocolVersion()) {
                message = "disconnectionScreen.outdatedClient";

                player.sendPlayStatus(PlayStatusPacket.Status.FAILED_CLIENT);
            } else {
                message = "disconnectionScreen.outdatedServer";

                player.sendPlayStatus(PlayStatusPacket.Status.FAILED_SERVER);
            }
            player.close("", message, false);
            return true;
        }
        player.getSession().setPacketCodec(packetCodec);

        player.setLoginChainData(ClientChainData.read(packet));


        if (!player.getLoginChainData().isXboxAuthed() && player.getServer().getPropertyBoolean("xbox-auth")) {
            player.close("", "disconnectionScreen.notAuthenticated");
            return true;
        }

        if (player.getServer().getOnlinePlayers().size() >= player.getServer().getMaxPlayers() && player.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
            return true;
        }

        player.setClientId(player.getLoginChainData().getClientId());

        player.setServerId(player.getLoginChainData().getClientUUID());

        String username = player.getLoginChainData().getUsername();
        boolean valid = true;
        int len = username.length();
        if (len > 16 || len < 3) {
            valid = false;
        }

        for (int i = 0; i < len && valid; i++) {
            char c = username.charAt(i);
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

        player.setName(TextFormat.clean(username));
        player.setDisplayName(player.getName());
        player.setLowerCaseName(player.getName().toLowerCase());
        player.setNameTag(player.getName());

        if (!valid || Objects.equals(player.getLowerCaseName(), "rcon") || Objects.equals(player.getLowerCaseName(), "console")) {
            player.close("", "disconnectionScreen.invalidName");
            return true;
        }

        if (!player.getLoginChainData().getSkin().isValid()) {
            player.close("", "disconnectionScreen.invalidSkin");
            return true;
        } else {
            player.setSkin(player.getLoginChainData().getSkin());
        }

        PlayerPreLoginEvent playerPreLoginEvent;
        player.getServer().getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(player, "Plugin reason"));
        if (playerPreLoginEvent.isCancelled()) {
            player.close("", playerPreLoginEvent.getKickMessage());
            return true;
        }

        Player playerInstance = player;
        player.setPreLoginEventTask(new AsyncTask() {

            private PlayerAsyncPreLoginEvent e;

            @Override
            public void onRun() {
                e = new PlayerAsyncPreLoginEvent(player.getName(), player.getServerId(), player.getSocketAddress());
                player.getServer().getPluginManager().callEvent(e);
            }

            @Override
            public void onCompletion(Server server) {
                if (!playerInstance.closed) {
                    if (e.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                        playerInstance.close(e.getKickMessage(), e.getKickMessage());
                    } else if (playerInstance.isShouldLogin()) {
                        playerInstance.completeLoginSequence();
                    }

                    for (Consumer<Server> action : e.getScheduledActions()) {
                        action.accept(server);
                    }
                }
            }
        });

        player.getServer().getScheduler().scheduleAsyncTask(player.getPreLoginEventTask());

        player.processLogin();
        return true;
    }
}
