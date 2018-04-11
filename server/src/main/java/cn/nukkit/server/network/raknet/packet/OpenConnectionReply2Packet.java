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

package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class OpenConnectionReply2Packet implements RakNetPacket {
    private long serverId;
    private InetSocketAddress clientAddress;
    private short mtuSize;
    private boolean serverSecurity;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeUnconnectedMagic(buffer);
        buffer.writeLong(serverId);
        RakNetUtil.writeAddress(buffer, clientAddress);
        buffer.writeShort(mtuSize);
        buffer.writeByte((serverSecurity ? 1 : 0));
    }

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverId = buffer.readLong();
        clientAddress = RakNetUtil.readAddress(buffer);
        mtuSize = buffer.readShort();
        serverSecurity = (buffer.readByte() != 0);
    }
}
