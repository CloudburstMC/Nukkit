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
public class ConnectionRequestAcceptedPacket implements RakNetPacket {
    private InetSocketAddress systemAddress;
    private short systemIndex;
    private InetSocketAddress[] systemAddresses;
    private long incomingTimestamp;
    private long systemTimestamp;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeAddress(buffer, systemAddress);
        buffer.writeShort(systemIndex);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeAddress(buffer, address);
        }
        buffer.writeLong(incomingTimestamp);
        buffer.writeLong(systemTimestamp);
    }

    @Override
    public void decode(ByteBuf buffer) {
        systemAddress = RakNetUtil.readAddress(buffer);
        systemIndex = buffer.readShort();
        systemAddresses = new InetSocketAddress[20];
        for (int i = 0; i < 10; i++) {
            systemAddresses[i] = RakNetUtil.readAddress(buffer);
        }
        incomingTimestamp = buffer.readLong();
        systemTimestamp = buffer.readLong();
    }
}
