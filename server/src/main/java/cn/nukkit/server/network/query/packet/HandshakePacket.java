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

package cn.nukkit.server.network.query.packet;

import cn.nukkit.server.network.query.QueryPacket;
import cn.nukkit.server.network.query.QueryUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HandshakePacket implements QueryPacket {
    private static final short ID = 0x09;
    // Both
    private int sessionId;
    // Response
    private String token;

    @Override
    public void decode(ByteBuf buffer) {
        sessionId = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(sessionId);
        QueryUtil.writeNullTerminatedString(buffer, token);
    }

    @Override
    public short getId() {
        return ID;
    }
}
