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

package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.JukeboxBlockEntity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.util.ItemTypeUtil;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Optional;

public class NukkitJukeboxBlockEntity implements JukeboxBlockEntity {
    private ItemInstance record;

    @Override
    public Optional<ItemInstance> getRecord() {
        return Optional.ofNullable(record);
    }

    @Override
    public void setRecord(@Nullable ItemInstance record) {
        if (!this.record.equals(record)) {
            if (record != null) {
                Preconditions.checkArgument(ItemTypeUtil.isRecord(record.getItemType()), "ItemType must be a record");
            }
            this.record = record;
            // onRecordChange(oldRecord, newRecord);
        }
    }

    public void removeRecord() {

    }

    private void play() {

    }
}
