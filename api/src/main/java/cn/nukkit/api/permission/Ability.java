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

package cn.nukkit.api.permission;

public enum Ability {
    /**
     * World cannot be interacted with
     */
    WORLD_IMMUTABLE,
    /**
     * Player cannot hit other players.
     */
    NO_PVP,
    /**
     * Player cannot hit mobs.
     */
    NO_PVM,
    /**
     * Mobs cannot hit player
     */
    NO_MVP,
    /**
     * Can auto jump
     */
    AUTO_JUMP,
    /**
     * Is allowed to fly
     */
    ALLOWED_FLIGHT,
    /**
     * Can fly through the world
     */
    NO_CLIP,
    /**
     * Can edit the world
     */
    WORLD_BUILDER,
    /**
     * Is flying
     */
    FLYING,

    /**
     * Can place and destroy blocks
     */
    PLACE_AND_DESTROY,
    /**
     * Can interact with doors and switches
     */
    DOORS_AND_SWITCHES,
    /**
     * Can open containers e.g. Chests
     */
    OPEN_CONTAINERS,
    /**
     * Can attack other players
     */
    ATTACK_PLAYERS,
    /**
     * Can attack mobs
     */
    ATTACK_MOBS,
    /**
     * Is an operator
     */
    OPERATOR,
    /**
     * Can teleport
     */
    TELEPORT
}
