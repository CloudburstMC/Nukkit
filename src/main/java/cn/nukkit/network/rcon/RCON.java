package cn.nukkit.network.rcon;

import cn.nukkit.Server;
import cn.nukkit.command.RemoteConsoleCommandSender;
import cn.nukkit.event.server.RemoteServerCommandEvent;
import cn.nukkit.utils.TextFormat;

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
    private Server server;
    private RCONServer serverThread;

    public RCON(Server server, String password, String address, int port) {
        if (password.isEmpty()) {
            server.getLogger().critical(server.getLanguage().translateString("nukkit.server.rcon.emptyPasswordError"));
            return;
        }

        this.server = server;

        try {
            this.serverThread = new RCONServer(address, port, password);
            this.serverThread.start();
        } catch (IOException exception) {
            this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.server.rcon.startupError", exception.getMessage()));
            return;
        }

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.rcon.running", new String[]{address, String.valueOf(port)}));
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
                String cmd = command.getCommand();
                if (cmd.startsWith("/")) {
                    // Convenience if user accidentally starts command with /.
                    cmd = cmd.substring(1);
                }

                this.server.dispatchCommand(sender, cmd);
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
