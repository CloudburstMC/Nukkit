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

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class CraftTransactionRecord extends TransactionRecord {
    public static final int TYPE_CRAFTING_ADD_INGREDIENT = -2;
    public static final int TYPE_CRAFTING_REMOVE_INGREDIENT = -2;
    public static final int TYPE_CRAFTING_RESULT = -2;
    public static final int TYPE_CRAFTING_USE_INGREDIENT = -2;
    public static final int TYPE_CONTAINER_DROP_CONTENTS = -100;
    private static final Type type = Type.CRAFT;
    private int inventoryId;

    @Override
    public void write(ByteBuf buffer){
        writeSignedInt(buffer, inventoryId);
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer){
        inventoryId = readSignedInt(buffer);
        super.read(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }
}
