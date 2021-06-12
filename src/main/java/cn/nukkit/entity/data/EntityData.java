package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityData<T> {
    private static final Int2ObjectMap<String> KNOWN_ENTITY_DATA = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<String> KNOWN_ENTITY_FLAGS = new Int2ObjectOpenHashMap<>();
    static {
        Arrays.stream(Entity.class.getDeclaredFields())
                .filter(it-> it.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                .filter(it-> it.getType().equals(int.class))
                .filter(it-> it.getName().startsWith("DATA_"))
                .filter(it-> !it.getName().startsWith("DATA_FLAG_"))
                .filter(it-> !it.getName().startsWith("DATA_TYPE_"))
                .forEachOrdered(it-> {
                    try {
                        KNOWN_ENTITY_DATA.put(it.getInt(null), it.getName().substring(5));
                    } catch (IllegalAccessException e) {
                        throw new InternalError(e);
                    }
                });

        Arrays.stream(Entity.class.getDeclaredFields())
                .filter(it-> it.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                .filter(it-> it.getType().equals(int.class))
                .filter(it-> it.getName().startsWith("DATA_FLAG_"))
                .forEachOrdered(it-> {
                    try {
                        KNOWN_ENTITY_FLAGS.put(it.getInt(null), it.getName().substring(10));
                    } catch (IllegalAccessException e) {
                        throw new InternalError(e);
                    }
                });
    }

    private int id;

    protected EntityData(int id) {
        this.id = id;
    }

    public abstract int getType();

    public abstract T getData();

    public abstract void setData(T data);

    public int getId() {
        return id;
    }

    public EntityData setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntityData && ((EntityData) obj).getId() == this.getId() && Objects.equals(((EntityData) obj).getData(), this.getData());
    }

    private SortedSet<String> readFlags(int offset, long flags) {
        SortedSet<String> flagSet = new TreeSet<>();
        int limit = offset + 64;
        for (Int2ObjectMap.Entry<String> flag : KNOWN_ENTITY_FLAGS.int2ObjectEntrySet()) {
            if (flag.getIntKey() >= limit) continue;
            long bit = 1L << (flag.getIntKey() - offset);
            if ((flags & bit) != 0) {
                flagSet.add(flag.getValue());
            }
        }
        return flagSet;
    }

    @Override
    public String toString() {
        String idStr = KNOWN_ENTITY_DATA.get(id);
        if (idStr == null) idStr = Integer.toString(id);
        Object data = getData();
        if (id == Entity.DATA_FLAGS) {
            data = readFlags(0, (long) data);
        } else if (id == Entity.DATA_FLAGS_EXTENDED) {
            data = readFlags(64, (long) data);
        }
        return getClass().getSimpleName()+"{" +
                "id=" + idStr + "," +
                "data=" + data +
                '}';
    }
}
