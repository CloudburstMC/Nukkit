package cn.nukkit.network.rcon;

import cn.nukkit.Server;
import cn.nukkit.command.RemoteConsoleCommandSender;
import cn.nukkit.event.server.RemoteServerCommandEvent;
import cn.nukkit.utils.TextFormat;

import java.io.IOException;

/**
 * Implementation of Source RCON protocol.
 * <a href="https://developer.valvesoftware.com/wiki/Source_RCON_Protocol">...</a>
 * <p>
 * Wrapper for RCONServer. Handles data.
 *
 * @author Tee7even
 */
public class RCON {

    private final Server server;
    private final RCONServer serverThread;

    public RCON(Server server, String password, String address, int port) {
        if (password.isEmpty()) {
            throw new IllegalArgumentException(server.getLanguage().translateString("nukkit.server.rcon.emptyPasswordError"));
        }

        this.server = server;

        try {
            this.serverThread = new RCONServer(address, port, password);
            this.serverThread.start();
        } catch (IOException e) {
            throw new IllegalArgumentException(server.getLanguage().translateString("nukkit.server.rcon.startupError"), e);
        }

        server.getLogger().info(server.getLanguage().translateString("nukkit.server.rcon.running", new String[]{address, String.valueOf(port)}));
        server.getLogger().warning("RCON is not secure! Please consider using other remote control solutions or at least make sure RCON is running behind a firewall.");
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
            sender.clearMessages();
        }
    }

    public void close() {
        try {
            synchronized (serverThread) {
                serverThread.close();
                serverThread.wait(5000);
            }
        } catch (InterruptedException ignored) {}
    }
}
