/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
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

package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.experimental.UtilityClass;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author joserobjr
 * @since 2021-03-26
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@UtilityClass
public class NukkitCollectors {
    /**
     * Returns a {@code Collector} accepting elements of type {@code T} that
     * counts the number of input elements.  If no elements are present, the
     * result is 0.
     *
     * @implSpec
     * This produces a result equivalent to:
     * <pre>{@code
     *     reducing(0, e -> 1, Integer::sum)
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} that counts the input elements
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static <T> Collector<T, ?, Integer> countingInt() {
        return Collectors.reducing(0, e -> 1, Integer::sum);
    }
}
