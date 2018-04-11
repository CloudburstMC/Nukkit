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

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.inventory.transaction.record.TransactionRecord;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
@EqualsAndHashCode
public abstract class ComplexTransaction implements InventoryTransaction {
    private final long creationTime;
    private final List<TransactionRecord> records = new ArrayList<>();
    private int slot;
    private ItemInstance item;
    private Vector3f fromPosition;

    public ComplexTransaction() {
        creationTime = System.currentTimeMillis();
    }

    public void read(ByteBuf buffer) {
        slot = readSignedInt(buffer);
        item = readItemInstance(buffer);
        fromPosition = readVector3f(buffer);
    }

    public void write(ByteBuf buffer) {
        writeSignedInt(buffer, slot);
        writeItemInstance(buffer, item);
        writeVector3f(buffer, fromPosition);
    }

    @Override
    public String toString() {
        return "(" +
                "type=" + getType() +
                ", records=" + Arrays.toString(getRecords().toArray()) +
                ", creationTime=" + creationTime +
                ", slot=" + slot +
                ", item=" + item +
                ", fromPosition=" + fromPosition;
    }
}
