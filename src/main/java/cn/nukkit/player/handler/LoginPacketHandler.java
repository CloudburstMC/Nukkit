package cn.nukkit.player.handler;

import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.BedrockInterface;
import cn.nukkit.network.ProtocolInfo;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.TextFormat;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Extollite
 */
@Log4j2
public class LoginPacketHandler implements BedrockPacketHandler {
    private final BedrockServerSession session;
    private final Server server;
    private final BedrockInterface interfaz;

    private ClientChainData chainData;

    public LoginPacketHandler(BedrockServerSession session, Server server, BedrockInterface interfaz) {
        this.session = session;
        this.interfaz = interfaz;
        this.server = server;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        if(!session.isLogging()){
            return true;
        }
        int protocolVersion = packet.getProtocolVersion();
        BedrockPacketCodec packetCodec = ProtocolInfo.getPacketCodec(protocolVersion);

        if (packetCodec == null) {
            String message;
            PlayStatusPacket statusPacket = new PlayStatusPacket();
            if (protocolVersion < ProtocolInfo.getDefaultProtocolVersion()) {
                message = "disconnectionScreen.outdatedClient";

                statusPacket.setStatus(PlayStatusPacket.Status.FAILED_CLIENT);
            } else {
                message = "disconnectionScreen.outdatedServer";

                statusPacket.setStatus(PlayStatusPacket.Status.FAILED_SERVER);
            }
            session.sendPacket(statusPacket);
            session.disconnect(message);
            return true;
        }
        session.setPacketCodec(packetCodec);

        this.chainData = ClientChainData.read(packet);


        if (!this.chainData.isXboxAuthed() && this.server.getPropertyBoolean("xbox-auth")) {
            session.disconnect("disconnectionScreen.notAuthenticated");
            return true;
        }

        String username = this.chainData.getUsername();
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

        username = TextFormat.clean(username);

        if (!valid || Objects.equals(username.toLowerCase(), "rcon") || Objects.equals(username.toLowerCase(), "console")) {
            session.disconnect("disconnectionScreen.invalidName");
            return true;
        }

        if (!this.chainData.getSkin().isValid()) {
            session.disconnect("disconnectionScreen.invalidSkin");
            return true;
        }

        Player player;

        session.setLogging(false);
        PlayerCreationEvent ev = new PlayerCreationEvent(interfaz, Player.class, Player.class, this.chainData.getClientId(), session.getAddress());
        this.server.getPluginManager().callEvent(ev);
        Class<? extends Player> clazz = ev.getPlayerClass();

        try {
            Constructor<? extends Player> constructor = clazz.getConstructor(BedrockServerSession.class);
            player = constructor.newInstance(session);
            this.server.addPlayer(session.getAddress(), player);
            session.addDisconnectHandler(interfaz.initDisconnectHandler(player));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.throwing(Level.ERROR, e);
            return true;
        }

        PlayerPreLoginEvent playerPreLoginEvent;
        this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(player, "Plugin reason"));
        if (playerPreLoginEvent.isCancelled()) {
            player.close("", playerPreLoginEvent.getKickMessage());
            return true;
        }

        Player playerInstance = player;
        player.setPreLoginEventTask(new AsyncTask() {

            private PlayerAsyncPreLoginEvent e;

            @Override
            public void onRun() {
                e = new PlayerAsyncPreLoginEvent(playerInstance.getName(), playerInstance.getServerId(), playerInstance.getSocketAddress());
                server.getPluginManager().callEvent(e);
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

        this.server.getScheduler().scheduleAsyncTask(player.getPreLoginEventTask());

        player.processLogin();
        return true;
    }
}
