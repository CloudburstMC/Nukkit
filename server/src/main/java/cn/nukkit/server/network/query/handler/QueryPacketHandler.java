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

package cn.nukkit.server.network.query.handler;

import cn.nukkit.api.event.server.RefreshQueryEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.query.QueryUtil;
import cn.nukkit.server.network.query.enveloped.DirectAddressedQueryPacket;
import cn.nukkit.server.network.query.packet.HandshakePacket;
import cn.nukkit.server.network.query.packet.StatisticsPacket;
import cn.nukkit.server.network.util.EncryptionUtil;
import cn.nukkit.server.util.NativeCodeFactory;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.server.jni.hash.VoxelwindHash;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

public class QueryPacketHandler extends SimpleChannelInboundHandler<DirectAddressedQueryPacket> {
    private static final ThreadLocal<VoxelwindHash> hashLocal = ThreadLocal.withInitial(NativeCodeFactory.hash::newInstance);
    private final Timer timer;
    private final NukkitServer server;
    private byte[] lastToken;
    private byte[] token;
    private ByteBuf shortStats;
    private ByteBuf longStats;

    public QueryPacketHandler(NukkitServer server) {
        this.server = server;
        this.timer = new Timer("QueryRegenerationTicker");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DirectAddressedQueryPacket packet) throws Exception {
        if (packet.content() instanceof HandshakePacket) {
            HandshakePacket handshake = (HandshakePacket) packet.content();
            handshake.setToken(getTokenString(packet.sender()));
            ctx.writeAndFlush(new DirectAddressedQueryPacket(handshake, packet.sender(), packet.recipient()), ctx.voidPromise());
        }
        if (packet.content() instanceof StatisticsPacket) {
            StatisticsPacket statistics = (StatisticsPacket) packet.content();
            if (!(statistics.getToken() == getTokenInt(packet.sender()))) {
                return;
            }

            if (statistics.isFull()) {
                statistics.setPayload(longStats);
            } else {
                statistics.setPayload(shortStats);
            }
            ctx.writeAndFlush(new DirectAddressedQueryPacket(statistics, packet.sender(), packet.recipient()), ctx.voidPromise());
        }
    }

    public void refreshToken() {
        lastToken = token;
        token = EncryptionUtil.generateRandomToken();
    }

    public void refreshInfo() {
        if (longStats != null) {
            longStats.release();
            shortStats.release();
        }

        RefreshQueryEvent event = new RefreshQueryEvent(server);
        server.getEventManager().fire(event);

        StringJoiner pluginJoiner = new StringJoiner(";");
        event.getPlugins().forEach(plugin -> pluginJoiner.add(plugin.getName() + " " + plugin.getVersion()));

        ByteBuf longStat = PooledByteBufAllocator.DEFAULT.buffer();
        ByteBuf shortStat = PooledByteBufAllocator.DEFAULT.buffer();
        longStat.writeBytes(QueryUtil.LONG_RESPONSE_PADDING_TOP);
        String gametype = "SMP";
        switch (event.getGameMode()) {
            case ADVENTURE:
                gametype = "AMP";
                break;
            case CREATIVE:
                gametype = "CMP";
                break;
        }
        ImmutableMap<String, String> kvs = ImmutableMap.<String, String>builder()
                .put("hostname", event.getServerName())
                .put("gametype", gametype)
                .put("map", event.getWorld())
                .put("numplayers", Integer.toString(event.getPlayerCount()))
                .put("maxplayers", Integer.toString(event.getMaxPlayerCount()))
                .put("hostport", Integer.toString(server.getConfiguration().getNetwork().getPort()))
                .put("hostip", server.getConfiguration().getNetwork().getAddress())
                .put("game_id", "MINECRAFTPE")
                .put("version", event.getVersion())
                .put("plugins", "Nukkit " + server.getMinecraftVersion() + "-API-" + server.getApiVersion() +
                        (event.isPluginListEnabled() ? ":" + pluginJoiner.toString() : ""))
                .put("whitelist", event.isWhitelisted() ? "on" : "off")
                .build();

        kvs.forEach((key, value) -> {
            QueryUtil.writeNullTerminatedString(longStat, key);
            QueryUtil.writeNullTerminatedString(longStat, value);
        });
        longStat.writeByte(0);

        kvs.values().asList().subList(0, 4).forEach(value -> QueryUtil.writeNullTerminatedString(shortStat, value));
        shortStat.writeShortLE(server.getConfiguration().getNetwork().getPort());
        QueryUtil.writeNullTerminatedString(shortStat, server.getConfiguration().getNetwork().getAddress());

        longStat.writeBytes(QueryUtil.LONG_RESPONSE_PADDING_BOTTOM);
        event.getPlayers().forEach(player -> QueryUtil.writeNullTerminatedString(longStat, player.getName()));
        longStat.writeByte(0);

        this.shortStats = shortStat;
        this.longStats = longStat;

        // schedule for next run.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshInfo();
            }
        }, event.getTimeout() * 1000);
    }

    private String getTokenString(InetSocketAddress socketAddress) {
        return Integer.toString(getTokenInt(socketAddress));

    }

    private int getTokenInt(InetSocketAddress socketAddress) {
        return ByteBuffer.wrap(getToken(socketAddress)).getInt();
    }

    private byte[] getToken(InetSocketAddress socketAddress) {
        VoxelwindHash hash = hashLocal.get();
        ByteBuf counterBuf = PooledByteBufAllocator.DEFAULT.directBuffer(20);
        ByteBuf keyBuf = PooledByteBufAllocator.DEFAULT.directBuffer(16);
        try {
            counterBuf.writeBytes(socketAddress.toString().getBytes(StandardCharsets.UTF_8));
            keyBuf.writeBytes(token);

            hash.update(counterBuf);
            hash.update(keyBuf);
            byte[] digested = hash.digest();
            return Arrays.copyOf(digested, 4);
        } finally {
            counterBuf.release();
            keyBuf.release();
        }
    }
}
