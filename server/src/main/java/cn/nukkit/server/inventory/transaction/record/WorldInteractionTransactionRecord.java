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

package cn.nukkit.server.inventory.transaction.record;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorldInteractionTransactionRecord extends TransactionRecord {
    private static final Type type = Type.WORLD_INTERACTION;
    private Action action;

    @Override
    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, action.ordinal());
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        action = Action.values()[readUnsignedInt(buffer)];
        super.read(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }

    public enum Action {
        DROP_ITEM,
        PICKUP_ITEM
    }
}
