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

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnull;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 * @author joserobjr
 * @since 2020-10-05
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ConvertingSetWrapper<V1, V2> extends AbstractSet<V1> {
    private final Function<V1, V2> converter;
    private final Function<V2, V1> reverseConverter;
    private final Set<V2> proxied;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ConvertingSetWrapper(Set<V2> proxied, Function<V1, V2> converter, Function<V2, V1> reverseConverter) {
        this.proxied = proxied;
        this.converter = converter;
        this.reverseConverter = reverseConverter;
    }

    @Override
    @Nonnull
    public Iterator<V1> iterator() {
        return new ConvertingIterator();
    }

    @Override
    public int size() {
        return proxied.size();
    }

    @Override
    public boolean isEmpty() {
        return proxied.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        Function uncheckedConverter = converter;
        Object converted = uncheckedConverter.apply(o);
        return proxied.contains(converted);
    }

    @Override
    public boolean add(V1 v1) {
        return proxied.add(converter.apply(v1));
    }

    @Override
    public boolean remove(Object o) {
        Function uncheckedConverter = converter;
        Object converted = uncheckedConverter.apply(o);
        return proxied.remove(converted);
    }

    @Override
    public void clear() {
        proxied.clear();
    }

    private class ConvertingIterator implements Iterator<V1> {
        private final Iterator<V2> proxiedIterator = proxied.iterator();

        @Override
        public void remove() {
            proxiedIterator.remove();
        }

        @Override
        public boolean hasNext() {
            return proxiedIterator.hasNext();
        }

        @Override
        public V1 next() {
            return reverseConverter.apply(proxiedIterator.next());
        }
    }
}
