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

package cn.nukkit.server.inventory.transaction;

import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static cn.nukkit.server.nbt.util.VarInt.*;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemUseTransaction extends ComplexTransaction {
    private static final Type type = Type.ITEM_USE;
    private Action action;
    private Vector3i position;
    private int face;
    private Vector3f clickPosition;

    public void read(ByteBuf buffer){
        action = Action.values()[readUnsignedInt(buffer)];
        position = readVector3i(buffer);
        face = readSignedInt(buffer);
        super.read(buffer);
        clickPosition = readVector3f(buffer);
    }

    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, action.ordinal());
        writeVector3i(buffer, position);
        writeSignedInt(buffer, face);
        super.write(buffer);
        writeVector3f(buffer, clickPosition);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void handle(PlayerSession.PlayerNetworkPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "ItemUseTransaction" + super.toString() +
                ", action=" + action +
                ", position=" + position +
                ", face=" + face +
                ", clickPosition=" + clickPosition +
                ')';
    }

    public enum Action {
        PLACE,
        USE,
        BREAK
    }
}
