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

package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Ageable;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnegative;
@RequiredArgsConstructor
public class AgeableComponent implements Ageable {
    private final int adultAge;
    private boolean stale;
    private int age;

    @Override
    @Nonnegative
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(@Nonnegative int age) {
        stale = true;
        this.age = age;
    }

    @Override
    public boolean isBaby() {
        return age < adultAge;
    }

    @Override
    public void setAdult() {
        stale = true;
        age = adultAge;
    }

    @Override
    public boolean isAdult() {
        return age >= adultAge;
    }
}
