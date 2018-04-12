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

import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class MovePlayerPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Vector3f position;
    private Rotation rotation;
    private Mode mode;
    private boolean onGround;
    private long ridingRuntimeEntityId;
    private TeleportationCause teleportationCause;
    private int unknown0;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3f(buffer, position);
        writeRotation(buffer, rotation);
        buffer.writeByte(mode.ordinal());
        buffer.writeBoolean(onGround);
        writeRuntimeEntityId(buffer, ridingRuntimeEntityId);
        if (mode == Mode.TELEPORT) {
            buffer.writeIntLE(teleportationCause.ordinal());
            buffer.writeIntLE(unknown0);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        position = readVector3f(buffer);
        rotation = readRotation(buffer);
        mode = Mode.values()[buffer.readByte()];
        onGround = buffer.readBoolean();
        ridingRuntimeEntityId = readRuntimeEntityId(buffer);
        if (mode == Mode.TELEPORT) {
            teleportationCause = TeleportationCause.values()[buffer.readIntLE()];
            unknown0 = buffer.readIntLE();
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Mode {
        NORMAL,
        RESET,
        TELEPORT,
        ROTATION
    }

    public enum TeleportationCause {
        UNKNOWN,
        PROJECTILE,
        CHORUS_FRUIT,
        COMMAND,
        BEHAVIOR,
        COUNT
    }
}
