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

package cn.nukkit.api.entity.vehicle;

import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface Minecart extends Entity {

    /**
     * Gets a minecart's damage.
     *
     * @return The damage
     */
    double getDamage();

    /**
     * Sets a minecart's damage.
     *
     * @param damage over 40 to "kill" a minecart
     */
    void setDamage(double damage);

    /**
     * Gets the maximum speed of a minecart. The speed is unrelated to the
     * velocity.
     *
     * @return The max speed
     */
    double getMaxSpeed();

    /**
     * Sets the maximum speed of a minecart. Must be nonnegative. Default is
     * 0.4D.
     *
     * @param speed The max speed
     */
    void setMaxSpeed(double speed);

    /**
     * Returns whether this minecart will slow down faster without a passenger
     * occupying it
     *
     * @return Whether it decelerates faster
     */
    boolean isSlowWhenEmpty();

    /**
     * Sets whether this minecart will slow down faster without a passenger
     * occupying it
     *
     * @param slow Whether it will decelerate faster
     */
    void setSlowWhenEmpty(boolean slow);

    /**
     * Sets the display block for this minecart.
     * Passing a null value will set the minecart to have no display block.
     *
     * @param material the material to set as display block.
     */
    //void setDisplayBlock(Material material); //TODO

    /**
     * Gets the display block for this minecart.
     * This function will return the type AIR if none is set.
     *
     * @return the block displayed by this minecart.
     */
    //Material getDisplayBlock(); //TODO

    /**
     * Gets the offset of the display block.
     *
     * @return the current block offset for this minecart.
     */
    int getDisplayBlockOffset();

    /**
     * Sets the offset of the display block.
     *
     * @param offset the block offset to set for this minecart.
     */
    void setDisplayBlockOffset(int offset);
}
