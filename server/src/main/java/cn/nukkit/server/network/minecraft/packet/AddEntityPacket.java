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
import cn.nukkit.server.entity.Attribute;
import cn.nukkit.server.entity.EntityLink;
import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.util.MetadataDictionary;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddEntityPacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private int entityType;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private final List<Attribute> entityAttributes = new ArrayList<>();
    private final MetadataDictionary metadata = new MetadataDictionary();
    private final List<EntityLink> links = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        VarInt.writeUnsignedInt(buffer, entityType);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeVector2f(buffer, rotation.getBodyRotation());
        writeEntityAttributes(buffer, entityAttributes);
        metadata.writeTo(buffer);
        writeEntityLinks(buffer, links);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
