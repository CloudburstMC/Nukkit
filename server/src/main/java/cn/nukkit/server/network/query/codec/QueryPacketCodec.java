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

package cn.nukkit.server.network.query.codec;

import cn.nukkit.server.network.query.QueryPacket;
import cn.nukkit.server.network.query.enveloped.DirectAddressedQueryPacket;
import cn.nukkit.server.network.query.packet.HandshakePacket;
import cn.nukkit.server.network.query.packet.StatisticsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Arrays;
import java.util.List;

public class QueryPacketCodec extends MessageToMessageCodec<DatagramPacket, DirectAddressedQueryPacket> {
    private static final byte[] QUERY_SIGNATURE = new byte[]{(byte) 0xFE, (byte) 0xFD};
    private static final int HANDSHAKE = 0x09;
    private static final short STATISTICS = 0x00;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DirectAddressedQueryPacket packet, List<Object> list) throws Exception {
        try {
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
            buf.writeByte(packet.content().getId() & 0xFF);
            packet.content().encode(buf);
            list.add(new DatagramPacket(buf, packet.recipient(), packet.sender()));
        } finally {
            packet.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket packet, List<Object> list) throws Exception {
        ByteBuf buf = packet.content();
        if (buf.readableBytes() < 3) {
            // not interested
            return;
        }
        buf.markReaderIndex();

        byte[] prefix = new byte[2];
        buf.readBytes(prefix);
        if (Arrays.equals(prefix, QUERY_SIGNATURE)) {
            short id = buf.readUnsignedByte();
            QueryPacket networkPacket;
            switch (id) {
                case HANDSHAKE:
                    networkPacket = new HandshakePacket();
                    break;
                case STATISTICS:
                    networkPacket = new StatisticsPacket();
                    break;
                default:
                    buf.resetReaderIndex();
                    return;
            }
            networkPacket.decode(buf);
            list.add(new DirectAddressedQueryPacket(networkPacket, packet.recipient(), packet.sender()));
        } else {
            buf.resetReaderIndex();
        }
    }
}
