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

package cn.nukkit.utils.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2020-10-05
 */
class ConvertingSetWrapperTest {
    
    private final Set<String> proxy = new HashSet<>();
    private final ConvertingSetWrapper<Integer, String> set = new ConvertingSetWrapper<>(proxy, Object::toString, Integer::parseInt);

    @BeforeEach
    void setUp() {
        proxy.clear();
    }

    @Test
    void iterator() {
        proxy.add("1");
        proxy.add("2");
        proxy.add("3");
        Iterator<Integer> iterator = set.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void size() {
        assertEquals(0, set.size());
        proxy.add("5");
        assertEquals(1, set.size());
    }

    @Test
    void isEmpty() {
        assertTrue(set.isEmpty());
        proxy.add("5");
        assertFalse(set.isEmpty());
    }

    @Test
    void contains() {
        assertFalse(set.contains(5));
        proxy.add("5");
        assertTrue(set.contains(5));
        
        assertThrows(ClassCastException.class, ()-> set.contains("5"));
    }

    @Test
    void add() {
        assertTrue(set.add(3));
        assertTrue(proxy.contains("3"));
        assertFalse(set.add(3));
    }

    @Test
    void remove() {
        assertFalse(set.remove(4));
        proxy.add("4");
        assertTrue(set.remove(4));
        assertFalse(proxy.contains("4"));
        assertFalse(set.remove(4));
        
        assertThrows(ClassCastException.class, ()-> set.remove("4"));
    }

    @Test
    void clear() {
        proxy.add("8");
        set.clear();
        assertTrue(proxy.isEmpty());
    }
}
