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
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class UpdateBlockPacket implements MinecraftPacket {
    public static final Set<Flag> FLAG_ALL = Sets.newHashSet(Flag.NEIGBORS, Flag.NETWORK);
    public static final Set<Flag> FLAG_ALL_PRIORITY = Sets.newHashSet(Flag.NEIGBORS, Flag.NETWORK, Flag.PRIORITY);
    private final Set<Flag> flags = new HashSet<>();
    private Vector3i blockPosition;
    private int blockId;
    private int metadata;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeUnsignedInt(buffer, blockId);
        int flagValue = 0;
        for (Flag flag: flags) {
            flagValue |= flag.id;
        }
        writeUnsignedInt(buffer, (flagValue << 4) | metadata);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Flag {
        NONE(0b0000),
        NEIGBORS(0b0001),
        NETWORK(0b0010),
        NOGRAPHIC(0b0100),
        PRIORITY(0b1000);

        private final int id;

        Flag(int id) {
            this.id = id;
        }
    }
}
