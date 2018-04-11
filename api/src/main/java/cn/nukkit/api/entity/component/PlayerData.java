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

package cn.nukkit.api.entity.component;

import cn.nukkit.api.permission.Abilities;
import cn.nukkit.api.permission.CommandPermission;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.Skin;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface PlayerData extends EntityComponent {

    @Nonnull
    Abilities getAbilities();

    GameMode getGameMode();

    float getEffectiveSpeed();

    void setGamemode(GameMode gamemode);

    Skin getSkin();

    void setSkin(Skin skin);

    float getSpeed();

    void setSpeed(@Nonnegative float speed);

    int getHunger();

    void setHunger(@Nonnegative int hunger);

    float getSaturation();

    void setSaturation(@Nonnegative float saturation);

    float getExhaustion();

    void setExhaustion(@Nonnegative float exhaustion);

    int getExperience();

    void setExperience(@Nonnegative int experience);

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    @Nonnull
    PlayerPermission getPlayerPermission();

    void setPlayerPermission(PlayerPermission playerPermission);

    @Nonnull
    CommandPermission getCommandPermission();

    void setCommandPermission(CommandPermission permission);
}
