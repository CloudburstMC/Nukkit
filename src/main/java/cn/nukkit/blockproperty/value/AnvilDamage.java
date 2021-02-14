/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.RequiredArgsConstructor;

/**
 * @author joserobjr
 * @since 2020-10-10
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public enum AnvilDamage {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    UNDAMAGED("Anvil"),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    SLIGHTLY_DAMAGED("Slightly Damaged Anvil"),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    VERY_DAMAGED("Very Damaged Anvil"),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    BROKEN("Broken Anvil");
    private final String englishName;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }
}
