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
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class CommandBlockUpdatePacket implements MinecraftPacket {
    private boolean block;
    private Vector3i blockPosition;
    private int commandBlockMode;
    private boolean redstoneMode;
    private boolean conditional;
    private long minecartRuntimeEntityId;
    private String command;
    private String lastOutput;
    private String name;
    private boolean outputTracked;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        block = buffer.readBoolean();
        if (block) {
            blockPosition = readVector3i(buffer);
            commandBlockMode = readUnsignedInt(buffer);
            redstoneMode = buffer.readBoolean();
            conditional = buffer.readBoolean();
        } else {
            minecartRuntimeEntityId = readRuntimeEntityId(buffer);
        }

        command = readString(buffer);
        lastOutput = readString(buffer);
        name = readString(buffer);
        outputTracked = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
