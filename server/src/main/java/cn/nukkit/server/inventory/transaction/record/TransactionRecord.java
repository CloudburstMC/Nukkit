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

import cn.nukkit.api.item.ItemInstance;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Nonnull;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemInstance;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemInstance;

@Data
public abstract class TransactionRecord {
    private int slot;
    private ItemInstance oldItem;
    private ItemInstance newItem;

    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, slot);
        writeItemInstance(buffer, oldItem);
        writeItemInstance(buffer, newItem);
    }

    public void read(ByteBuf buffer){
        slot = readUnsignedInt(buffer);
        oldItem = readItemInstance(buffer);
        newItem = readItemInstance(buffer);
    }

    public abstract Type getType();

    public enum Type {
        CONTAINER,
        GLOBAL,
        WORLD_INTERACTION,
        CREATIVE,
        CRAFT(99999);

        private static final TIntObjectMap<Type> BY_ID = new TIntObjectHashMap<>(5);
        @Getter
        private final int id;

        Type() {
            id = ordinal();
            add();
        }

        Type(int id) {
            this.id = id;
            add();
        }

        @Nonnull
        public static Type byId(int id) {
            Type type = BY_ID.get(id);
            if (type == null) {
                throw new IllegalArgumentException(id + " is not a valid inventory source type!");
            }
            return type;
        }

        private void add() {
            BY_ID.put(id, this);
        }
    }
}