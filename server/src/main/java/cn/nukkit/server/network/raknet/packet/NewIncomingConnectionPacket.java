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
public class NewIncomingConnectionPacket implements RakNetPacket {
    private InetSocketAddress clientAddress;
    private InetSocketAddress[] systemAddresses;
    private long clientTimestamp;
    private long serverTimestamp;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeAddress(buffer, clientAddress);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeAddress(buffer, address);
        }
        buffer.writeLong(clientTimestamp);
        buffer.writeLong(serverTimestamp);
    }

    @Override
    public void decode(ByteBuf buffer) {
        clientAddress = RakNetUtil.readAddress(buffer);
        systemAddresses = new InetSocketAddress[20];
        for (int i = 0; i < 20; i++) {
            systemAddresses[i] = RakNetUtil.readAddress(buffer);
        }
        clientTimestamp = buffer.readLong();
        serverTimestamp = buffer.readLong();
    }
}
