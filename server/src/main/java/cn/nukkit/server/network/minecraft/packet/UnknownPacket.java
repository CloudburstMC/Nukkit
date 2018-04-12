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

package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class UnknownPacket implements MinecraftPacket {
    private short id;
    private ByteBuf payload;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(id);
        buffer.writeBytes(payload);
    }

    @Override
    public void decode(ByteBuf buffer) {
        id = buffer.readUnsignedByte();
        payload = buffer.readBytes(buffer.readableBytes());
    }

    @Override
    public String toString() {
        return "UNKNOWN - " + Integer.toHexString(id) + " - Hex: " + ByteBufUtil.hexDump(payload);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        if (log.isDebugEnabled()) {
            log.debug("Unknown packet received with ID " + Integer.toHexString(id));
            log.debug("Dump: {}", ByteBufUtil.hexDump(payload));
        }
        payload.release();
    }
}
