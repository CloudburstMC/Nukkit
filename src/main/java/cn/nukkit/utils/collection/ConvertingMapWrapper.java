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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author joserobjr
 * @since 2020-10-05
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ConvertingMapWrapper<K, V1, V2> extends AbstractMap<K, V1> {
    private final Function<V1, V2> converter;
    private final Function<V2, V1> reverseConverter;
    private final Map<K, V2> proxied;
    private final ConvertingSetWrapper<Entry<K, V1>, Entry<K, V2>> entrySet;
    private final boolean convertReturnedNulls;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ConvertingMapWrapper(Map<K, V2> proxied, Function<V1, V2> converter, Function<V2, V1> reverseConverter, boolean convertReturnedNulls) {
        this.proxied = proxied;
        this.converter = converter;
        this.reverseConverter = reverseConverter;
        this.convertReturnedNulls = convertReturnedNulls;
        entrySet = new ConvertingSetWrapper<>(
                proxied.entrySet(),
                entry -> new EntryWrapper<>(entry, reverseConverter, converter),
                entry -> new EntryWrapper<>(entry, converter, reverseConverter)
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ConvertingMapWrapper(Map<K, V2> proxied, Function<V1, V2> converter, Function<V2, V1> reverseConverter) {
        this(proxied, converter, reverseConverter, false);
    }
    

    @Override
    public Set<Entry<K, V1>> entrySet() {
        return entrySet;
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
    public boolean containsValue(Object value) {
        Function uncheckedConverter = converter;
        Object converted = uncheckedConverter.apply(value);
        return proxied.containsValue(converted);
    }

    @Override
    public boolean containsKey(Object key) {
        return proxied.containsKey(key);
    }

    @Override
    public V1 get(Object key) {
        V2 found = proxied.get(key);
        if (found == null && !convertReturnedNulls) {
            return null;
        }
        return reverseConverter.apply(found);
    }

    @Override
    public V1 put(K key, V1 value) {
        V2 removed = proxied.put(key, converter.apply(value));
        if (removed == null && !convertReturnedNulls) {
            return null;
        }
        return reverseConverter.apply(removed);
    }

    @Override
    public V1 remove(Object key) {
        V2 removed = proxied.remove(key);
        if (removed == null && !convertReturnedNulls) {
            return null;
        }
        return reverseConverter.apply(removed);
    }

    @Override
    public boolean remove(Object key, Object value) {
        Function uncheckedConverter = converter;
        Object converted = uncheckedConverter.apply(value);
        return proxied.remove(key, converted);
    }

    @Override
    public void clear() {
        proxied.clear();
    }

    @Override
    public Set<K> keySet() {
        return proxied.keySet();
    }

    private class EntryWrapper<E1, E2> implements Map.Entry<K, E1> {
        private final Function<E1, E2> entryConverter;
        private final Function<E2, E1> entryReverseConverter;
        private final Map.Entry<K, E2> entryProxied;

        public EntryWrapper(Entry<K, E2> entryProxied, Function<E1, E2> entryConverter, Function<E2, E1> entryReverseConverter) {
            this.entryConverter = entryConverter;
            this.entryReverseConverter = entryReverseConverter;
            this.entryProxied = entryProxied;
        }

        @Override
        public K getKey() {
            return entryProxied.getKey();
        }

        @Override
        public E1 getValue() {
            E2 value = entryProxied.getValue();
            if (value == null && !convertReturnedNulls) {
                return null;
            }
            return entryReverseConverter.apply(value);
        }

        @Override
        public E1 setValue(E1 value) {
            E2 newValue = entryConverter.apply(value);
            E2 oldValue = entryProxied.setValue(newValue);
            if (oldValue == null && !convertReturnedNulls) {
                return null;
            }
            return entryReverseConverter.apply(oldValue);
        }

        @Override
        public String toString() {
            return entryProxied.getKey()+"="+getValue();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                return Objects.equals(entryProxied.getKey(), e.getKey()) && Objects.equals(getValue(), e.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(entryProxied.getKey()) ^ Objects.hashCode(getValue());
        }
    }
}
