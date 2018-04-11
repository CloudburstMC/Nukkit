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

import cn.nukkit.server.nbt.NBTEncodingType;
import cn.nukkit.server.nbt.stream.NBTInputStream;
import cn.nukkit.server.nbt.stream.NBTOutputStream;
import cn.nukkit.server.nbt.tag.Tag;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.util.LittleEndianByteBufInputStream;
import cn.nukkit.server.network.util.LittleEndianByteBufOutputStream;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readVector3i;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class BlockEntityDataPacket implements MinecraftPacket {
    private Vector3i blockPostion;
    private Tag<?> data;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPostion);
        try (NBTOutputStream writer = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.BEDROCK)) {
            writer.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPostion = readVector3i(buffer);
        try (NBTInputStream reader = new NBTInputStream(new LittleEndianByteBufInputStream(buffer), NBTEncodingType.BEDROCK)) {
            data = reader.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
