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

package cn.nukkit.server.network.raknet.enveloped;

import cn.nukkit.server.network.raknet.RakNetPacket;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedRakNetPacket extends DefaultAddressedEnvelope<RakNetPacket, InetSocketAddress> {
    public DirectAddressedRakNetPacket(RakNetPacket message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedRakNetPacket(RakNetPacket message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
