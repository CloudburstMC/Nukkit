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

package cn.nukkit.server.block.behavior;

import cn.nukkit.api.block.BlockType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BlockBehaviors {
    private static final Map<BlockType, BlockBehavior> BLOCK_BEHAVIORS;

    static {
        BLOCK_BEHAVIORS = ImmutableMap.<BlockType, BlockBehavior>builder()
                .build();
    }

    public static BlockBehavior getBlockBehavior(BlockType type) {
        Preconditions.checkNotNull(type, "type");
        BlockBehavior behavior = BLOCK_BEHAVIORS.get(type);
        return behavior == null ? SimpleBlockBehavior.INSTANCE : behavior;
    }
}
