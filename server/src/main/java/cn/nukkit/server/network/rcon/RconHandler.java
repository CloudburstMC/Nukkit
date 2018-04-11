/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.rcon;

import cn.nukkit.api.command.CommandException;
import cn.nukkit.api.command.CommandNotFoundException;
import cn.nukkit.api.util.TextFormat;
import cn.nukkit.server.NukkitServer;
import com.google.common.base.Joiner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Log4j2
public class RconHandler extends SimpleChannelInboundHandler<RconMessage> {
    private final NukkitServer server;
    private final RconNetworkListener listener;
    private final byte[] password;
    private final RconConsoleCommandSender sender;
    private boolean authed = false;

    public RconHandler(NukkitServer server, RconNetworkListener listener, byte[] password) {
        this.server = server;
        this.listener = listener;
        this.password = password;
        this.sender = new RconConsoleCommandSender(server);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RconMessage rconMessage) throws Exception {
        if (!authed) {
            if (rconMessage.getType() != RconMessage.AUTH) {
                return;
            }

            byte[] sentPassword = rconMessage.getBody().getBytes(StandardCharsets.UTF_8);

            ctx.channel().writeAndFlush(new RconMessage(rconMessage.getId(), RconMessage.RESPONSE_VALUE, ""));

            if (MessageDigest.isEqual(password, sentPassword)) {
                authed = true;
                ctx.channel().writeAndFlush(new RconMessage(rconMessage.getId(), RconMessage.AUTH_RESPONSE, ""));
            } else {
                ctx.channel().writeAndFlush(new RconMessage(-1, RconMessage.AUTH_RESPONSE, ""));
            }
        } else if (rconMessage.getType() == RconMessage.EXECCOMMAND) {
            Channel channel = ctx.channel();

            listener.getCommandExecutionService().execute(() -> {
                String output;
                try {
                    server.getCommandManager().executeCommand(sender, rconMessage.getBody());
                    output = TextFormat.removeFormatting(Joiner.on('\n').join(sender.getMessages()));
                } catch (CommandNotFoundException e) {
                    output = server.getLocaleManager().replaceI18n("commands.generic.unknown", rconMessage.getBody().split(" ")[0]);
                } catch (CommandException e) {
                    output = server.getLocaleManager().replaceI18n("commands.generic.exception");
                    log.error("An error occurred whilst trying to execute command for RCON client {}, \n{}", channel.remoteAddress(), e);
                }
                channel.writeAndFlush(new RconMessage(rconMessage.getId(), RconMessage.RESPONSE_VALUE, output), ctx.voidPromise());
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("An error occurred whilst handling an RCON packet for {}, \n{}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
