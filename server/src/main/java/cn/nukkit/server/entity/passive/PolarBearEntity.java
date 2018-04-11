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

package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.PolarBear;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class PolarBearEntity extends LivingEntity implements PolarBear {

    public static final int NETWORK_ID = 28;

    public PolarBearEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.POLAR_BEAR, position, level, server, 30);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH), Item.get(Item.RAW_SALMON)};
    }*/
}
