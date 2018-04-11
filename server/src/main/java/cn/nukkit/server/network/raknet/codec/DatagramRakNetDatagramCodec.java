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

package cn.nukkit.server.network.raknet.codec;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import cn.nukkit.server.network.raknet.datagram.RakNetDatagram;
import cn.nukkit.server.network.raknet.datagram.RakNetDatagramFlags;
import cn.nukkit.server.network.raknet.enveloped.AddressedRakNetDatagram;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class DatagramRakNetDatagramCodec extends MessageToMessageCodec<DatagramPacket, AddressedRakNetDatagram> {
    private final NukkitServer server;

    public DatagramRakNetDatagramCodec(NukkitServer server) {
        this.server = server;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram, List<Object> list) throws Exception {
        ByteBuf buf = ctx.alloc().directBuffer();
        datagram.content().encode(buf);
        list.add(new DatagramPacket(buf, datagram.recipient(), datagram.sender()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> list) throws Exception {
        // Requires a session
        MinecraftSession session = server.getSessionManager().get(packet.sender());

        if (session == null) {
            return;
        }

        packet.content().markReaderIndex();
        RakNetDatagramFlags flags = new RakNetDatagramFlags(packet.content().readByte());
        packet.content().resetReaderIndex();

        if (flags.isValid() && !flags.isAck() && !flags.isNak()) {
            RakNetDatagram datagram = new RakNetDatagram();
            datagram.decode(packet.content());
            list.add(new AddressedRakNetDatagram(datagram, packet.recipient(), packet.sender()));
        }
    }
}
