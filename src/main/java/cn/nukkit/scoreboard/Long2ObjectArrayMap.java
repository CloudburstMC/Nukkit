package cn.nukkit.scoreboard;

/**
 * @author Erik Miller
 * @version 1.0
 */
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Long2ObjectArrayMap<V> extends AbstractLong2ObjectMap<V> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] key;
    private transient Object[] value;
    private int size;

    public Long2ObjectArrayMap( long[] key, Object[] value ) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if ( key.length != value.length ) {
            throw new IllegalArgumentException( "Keys and values have different lengths (" + key.length + ", " + value.length + ")" );
        }
    }

    public Long2ObjectArrayMap() {
        this.key = LongArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Long2ObjectArrayMap( int capacity ) {
        this.key = new long[capacity];
        this.value = new Object[capacity];
    }

    public Long2ObjectArrayMap( Long2ObjectMap<V> m ) {
        this( m.size() );
        this.putAll( m );
    }

    public Long2ObjectArrayMap( Map<? extends Long, ? extends V> m ) {
        this( m.size() );
        this.putAll( m );
    }

    public Long2ObjectArrayMap( long[] key, Object[] value, int size ) {
        this.key = key;
        this.value = value;
        this.size = size;
        if ( key.length != value.length ) {
            throw new IllegalArgumentException( "Keys and values have different lengths (" + key.length + ", " + value.length + ")" );
        } else if ( size > key.length ) {
            throw new IllegalArgumentException( "The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")" );
        }
    }

    public FastEntrySet<V> long2ObjectEntrySet() {
        return new EntrySet();
    }

    private int findKey( long k ) {
        long[] key = this.key;
        int i = this.size;

        do {
            if ( i-- == 0 ) {
                return -1;
            }
        } while ( key[i] != k );

        return i;
    }

    public V get( long k ) {
        long[] key = this.key;
        int i = this.size;

        do {
            if ( i-- == 0 ) {
                return this.defRetValue;
            }
        } while ( key[i] != k );

        return (V) this.value[i];
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        for ( int i = this.size; i-- != 0; this.value[i] = null ) {
        }

        this.size = 0;
    }

    public boolean containsKey( long k ) {
        return this.findKey( k ) != -1;
    }

    public boolean containsValue( Object v ) {
        int i = this.size;

        do {
            if ( i-- == 0 ) {
                return false;
            }
        } while ( !Objects.equals( this.value[i], v ) );

        return true;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public V put( long k, V v ) {
        int oldKey = this.findKey( k );
        if ( oldKey != -1 ) {
            V oldValue = (V) this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        } else {
            if ( this.size == this.key.length ) {
                long[] newKey = new long[this.size == 0 ? 2 : this.size * 2];
                Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];

                for ( int i = this.size; i-- != 0; newValue[i] = this.value[i] ) {
                    newKey[i] = this.key[i];
                }

                this.key = newKey;
                this.value = newValue;
            }

            this.key[this.size] = k;
            this.value[this.size] = v;
            ++this.size;
            return this.defRetValue;
        }
    }

    public V remove( long k ) {
        int oldPos = this.findKey( k );
        if ( oldPos == -1 ) {
            return this.defRetValue;
        } else {
            V oldValue = (V) this.value[oldPos];
            int tail = this.size - oldPos - 1;
            System.arraycopy( this.key, oldPos + 1, this.key, oldPos, tail );
            System.arraycopy( this.value, oldPos + 1, this.value, oldPos, tail );
            --this.size;
            this.value[this.size] = null;
            return oldValue;
        }
    }

    public LongSet keySet() {
        return new AbstractLongSet() {
            public boolean contains( long k ) {
                return Long2ObjectArrayMap.this.findKey( k ) != -1;
            }

            public boolean remove( long k ) {
                int oldPos = Long2ObjectArrayMap.this.findKey( k );
                if ( oldPos == -1 ) {
                    return false;
                } else {
                    int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
                    System.arraycopy( Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail );
                    System.arraycopy( Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail );
                    Long2ObjectArrayMap.this.size--;
                    return true;
                }
            }

            public LongIterator iterator() {
                return new LongIterator() {
                    int pos = 0;

                    public boolean hasNext() {
                        return this.pos < Long2ObjectArrayMap.this.size;
                    }

                    public long nextLong() {
                        if ( !this.hasNext() ) {
                            throw new NoSuchElementException();
                        } else {
                            return Long2ObjectArrayMap.this.key[this.pos++];
                        }
                    }

                    public void remove() {
                        if ( this.pos == 0 ) {
                            throw new IllegalStateException();
                        } else {
                            int tail = Long2ObjectArrayMap.this.size - this.pos;
                            System.arraycopy( Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail );
                            System.arraycopy( Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail );
                            Long2ObjectArrayMap.this.size--;
                        }
                    }
                };
            }

            public int size() {
                return Long2ObjectArrayMap.this.size;
            }

            public void clear() {
                Long2ObjectArrayMap.this.clear();
            }
        };
    }

    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>() {
            public boolean contains( Object v ) {
                return Long2ObjectArrayMap.this.containsValue( v );
            }

            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>() {
                    int pos = 0;

                    public boolean hasNext() {
                        return this.pos < Long2ObjectArrayMap.this.size;
                    }

                    public V next() {
                        if ( !this.hasNext() ) {
                            throw new NoSuchElementException();
                        } else {
                            return (V) Long2ObjectArrayMap.this.value[this.pos++];
                        }
                    }

                    public void remove() {
                        if ( this.pos == 0 ) {
                            throw new IllegalStateException();
                        } else {
                            int tail = Long2ObjectArrayMap.this.size - this.pos;
                            System.arraycopy( Long2ObjectArrayMap.this.key, this.pos, Long2ObjectArrayMap.this.key, this.pos - 1, tail );
                            System.arraycopy( Long2ObjectArrayMap.this.value, this.pos, Long2ObjectArrayMap.this.value, this.pos - 1, tail );
                            Long2ObjectArrayMap.this.size--;
                        }
                    }
                };
            }

            public int size() {
                return Long2ObjectArrayMap.this.size;
            }

            public void clear() {
                Long2ObjectArrayMap.this.clear();
            }
        };
    }

    public Long2ObjectArrayMap<V> clone() {
        Long2ObjectArrayMap c;
        try {
            c = (Long2ObjectArrayMap) super.clone();
        } catch ( CloneNotSupportedException var3 ) {
            throw new InternalError();
        }

        c.key = (long[]) this.key.clone();
        c.value = (Object[]) this.value.clone();
        return c;
    }

    private void writeObject( ObjectOutputStream s ) throws IOException {
        s.defaultWriteObject();

        for ( int i = 0; i < this.size; ++i ) {
            s.writeLong( this.key[i] );
            s.writeObject( this.value[i] );
        }

    }

    private void readObject( ObjectInputStream s ) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new long[this.size];
        this.value = new Object[this.size];

        for ( int i = 0; i < this.size; ++i ) {
            this.key[i] = s.readLong();
            this.value[i] = s.readObject();
        }

    }

    public class BasicEntry<V> implements Entry<V> {
        public long key;
        public V value;

        public BasicEntry() {
        }

        public BasicEntry( Long key, V value ) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry( long key, V value ) {
            this.key = key;
            this.value = value;
        }

        public long getLongKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue( V value ) {
            throw new UnsupportedOperationException();
        }
    }

    public class EntrySet extends AbstractObjectSet<Entry<V>> implements FastEntrySet<V> {
        private EntrySet() {
        }

        public ObjectIterator<Entry<V>> iterator() {
            return new ObjectIterator<Entry<V>>() {
                int curr = -1;
                int next = 0;

                public boolean hasNext() {
                    return this.next < Long2ObjectArrayMap.this.size;
                }

                public Entry<V> next() {
                    if ( !this.hasNext() ) {
                        throw new NoSuchElementException();
                    } else {
                        return new BasicEntry( Long2ObjectArrayMap.this.key[this.curr = this.next], Long2ObjectArrayMap.this.value[this.next++] );
                    }
                }

                public void remove() {
                    if ( this.curr == -1 ) {
                        throw new IllegalStateException();
                    } else {
                        this.curr = -1;
                        int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                        System.arraycopy( Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail );
                        System.arraycopy( Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail );
                        Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
                    }
                }
            };
        }

        public ObjectIterator<Entry<V>> fastIterator() {
            return new ObjectIterator<Entry<V>>() {
                int next = 0;
                int curr = -1;
                BasicEntry<V> entry = new BasicEntry();

                public boolean hasNext() {
                    return this.next < Long2ObjectArrayMap.this.size;
                }

                public Entry<V> next() {
                    if ( !this.hasNext() ) {
                        throw new NoSuchElementException();
                    } else {
                        this.entry.key = Long2ObjectArrayMap.this.key[this.curr = this.next];
                        this.entry.value = (V) Long2ObjectArrayMap.this.value[this.next++];
                        return this.entry;
                    }
                }

                public void remove() {
                    if ( this.curr == -1 ) {
                        throw new IllegalStateException();
                    } else {
                        this.curr = -1;
                        int tail = Long2ObjectArrayMap.this.size-- - this.next--;
                        System.arraycopy( Long2ObjectArrayMap.this.key, this.next + 1, Long2ObjectArrayMap.this.key, this.next, tail );
                        System.arraycopy( Long2ObjectArrayMap.this.value, this.next + 1, Long2ObjectArrayMap.this.value, this.next, tail );
                        Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
                    }
                }
            };
        }

        public int size() {
            return Long2ObjectArrayMap.this.size;
        }

        public boolean contains( Object o ) {
            if ( !( o instanceof java.util.Map.Entry ) ) {
                return false;
            } else {
                java.util.Map.Entry<?, ?> e = (java.util.Map.Entry) o;
                if ( e.getKey() != null && e.getKey() instanceof Long ) {
                    long k = (Long) ( (Long) e.getKey() );
                    return Long2ObjectArrayMap.this.containsKey( k ) && Objects.equals( Long2ObjectArrayMap.this.get( k ), e.getValue() );
                } else {
                    return false;
                }
            }
        }

        public boolean remove( Object o ) {
            if ( !( o instanceof java.util.Map.Entry ) ) {
                return false;
            } else {
                java.util.Map.Entry<?, ?> e = (java.util.Map.Entry) o;
                if ( e.getKey() != null && e.getKey() instanceof Long ) {
                    long k = (Long) ( (Long) e.getKey() );
                    V v = (V) e.getValue();
                    int oldPos = Long2ObjectArrayMap.this.findKey( k );
                    if ( oldPos != -1 && Objects.equals( v, Long2ObjectArrayMap.this.value[oldPos] ) ) {
                        int tail = Long2ObjectArrayMap.this.size - oldPos - 1;
                        System.arraycopy( Long2ObjectArrayMap.this.key, oldPos + 1, Long2ObjectArrayMap.this.key, oldPos, tail );
                        System.arraycopy( Long2ObjectArrayMap.this.value, oldPos + 1, Long2ObjectArrayMap.this.value, oldPos, tail );
                        Long2ObjectArrayMap.this.size--;
                        Long2ObjectArrayMap.this.value[Long2ObjectArrayMap.this.size] = null;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
