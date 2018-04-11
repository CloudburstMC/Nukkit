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

package cn.nukkit.api.level.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static cn.nukkit.api.level.data.GameRule.Type.BOOLEAN;

@RequiredArgsConstructor
@Getter
public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput", BOOLEAN),
    DO_DAYLIGHT_CYCLE("doDaylightCycle", BOOLEAN),
    DO_ENTITY_DROPS("doEntityDrops", BOOLEAN),
    DO_FIRE_TICK("doFireTick", BOOLEAN),
    DO_MOB_LOOT("doMobLoot", BOOLEAN),
    DO_MOB_SPAWNING("doMobSpawning", BOOLEAN),
    DO_TILE_DROPS("doTileDrops", BOOLEAN),
    DO_WEATHER_CYCLE("doWeatherCycle", BOOLEAN),
    DROWNING_DAMAGE("drowningDamage", BOOLEAN),
    FALL_DAMAGE("fallDamage", BOOLEAN),
    FIRE_DAMAGE("fireDamage", BOOLEAN),
    KEEP_INVENTORY("keepInventory", BOOLEAN),
    MOB_GRIEFING("mobGriefing", BOOLEAN),
    PVP("pvp", BOOLEAN),
    SHOW_COORDINATES("showCoordinates", BOOLEAN),
    NATURAL_REGENERATION("naturalRegeneration", BOOLEAN),
    TNT_EXPLODES("tntExplodes", BOOLEAN),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback", BOOLEAN);

    private final String name;
    private final Type type;

    public enum Type {
        UNKNOWN,
        BOOLEAN,
        INTEGER,
        FLOAT
    }
}
