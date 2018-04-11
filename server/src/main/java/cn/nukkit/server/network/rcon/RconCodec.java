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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class RconCodec extends ByteToMessageCodec<RconMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RconMessage rconMessage, ByteBuf byteBuf) throws Exception {
        byteBuf.writeIntLE(rconMessage.getId());
        byteBuf.writeIntLE(rconMessage.getType());
        ByteBufUtil.writeAscii(byteBuf, rconMessage.getBody());
        // 2 null bytes
        byteBuf.writeByte(0);
        byteBuf.writeByte(0);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int id = byteBuf.readIntLE();
        int type = byteBuf.readIntLE();
        String body = readNullTerminatedString(byteBuf);

        // Discard remaining bytes
        byteBuf.readerIndex(byteBuf.writerIndex());

        list.add(new RconMessage(id, type, body));
    }

    private String readNullTerminatedString(ByteBuf in) {
        StringBuilder read = new StringBuilder();
        byte readIn;
        while ((readIn = in.readByte()) != '\0') {
            read.append((char) readIn);
        }
        return read.toString();
    }
}
