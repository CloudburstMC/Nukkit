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

package cn.nukkit.api.level;

import cn.nukkit.api.level.data.GameRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GameRules {

    void setGameRule(@Nonnull GameRule gameRule, boolean value);

    void setGameRule(@Nonnull GameRule gameRule, int value);

    void setGameRule(@Nonnull GameRule gameRule, float value);

    boolean getBoolean(@Nonnull GameRule gameRule);

    int getInteger(@Nonnull GameRule gameRule);

    float getFloat(@Nonnull GameRule gameRule);

    @Nonnull
    String getString(@Nonnull GameRule gameRule);

    boolean contains(@Nullable GameRule gameRule);

    GameRule[] getRules();
}
