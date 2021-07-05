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

package cn.nukkit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-06-29
 */
class UtilsTest {

    @Test
    void isNumeric() {
        assertFalse(Utils.isInteger(null));
        assertFalse(Utils.isInteger(""));
        assertFalse(Utils.isInteger("-"));
        assertTrue(Utils.isInteger("-3"));
        assertFalse(Utils.isInteger("-3a"));
        assertTrue(Utils.isInteger("5"));
        assertFalse(Utils.isInteger("!"));
    }
}
