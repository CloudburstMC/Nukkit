package cn.nukkit.server.network.rcon;

import cn.nukkit.api.event.server.RemoteServerCommandEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.command.RemoteConsoleCommandSender;
import cn.nukkit.server.utils.TextFormat;

import java.io.IOException;

/**
 * Implementation of Source RCON protocol.
 * https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
 * <p>
 * Wrapper for RCONServer. Handles data.
 *
 * @author Tee7even
 */
public class RCON {
    private NukkitServer server;
    private RCONServer serverThread;

    public RCON(NukkitServer server, String password, String address, int port) {
        if (password.isEmpty()) {
            server.getLogger().critical(server.getLanguage().translateString("nukkit.server.rcon.emptyPasswordError"));
            return;
        }

        this.server = server;

        try {
            this.serverThread = new RCONServer(address, port, password);
            this.serverThread.start();
        } catch (IOException exception) {
            log.error(this.server.getLanguage().translateString("nukkit.server.rcon.startupError", exception.getMessage()));
            return;
        }

        log.info(this.server.getLanguage().translateString("nukkit.server.rcon.running", new String[]{address, String.valueOf(port)}));
    }

    public void check() {
        if (this.serverThread == null) {
            return;
        } else if (!this.serverThread.isAlive()) {
            return;
        }

        RCONCommand command;
        while ((command = serverThread.receive()) != null) {
            RemoteConsoleCommandSender sender = new RemoteConsoleCommandSender();
            RemoteServerCommandEvent event = new RemoteServerCommandEvent(sender, command.getCommand());
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.server.dispatchCommand(sender, command.getCommand());
            }

            this.serverThread.respond(command.getSender(), command.getId(), TextFormat.clean(sender.getMessages()));
        }
    }

    public void close() {
        try {
            synchronized (serverThread) {
                serverThread.close();
                serverThread.wait(5000);
            }
        } catch (InterruptedException exception) {
            //
        }
    }
}
