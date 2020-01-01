package cn.nukkit.entity.data;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Log4j2
@ToString
public class EntityFlags {

    private final Set<EntityFlag> flags = new HashSet<>();

    public static EntityFlags create(long value, int index) {
        EntityFlags flags = new EntityFlags();
        final int lower = index * 64;
        final int upper = lower + 64;
        for (int i = lower; i < upper; i++) {
            int idx = i & 0x3f;
            if ((value & (1L << idx)) != 0) {
                EntityFlag flag = EntityFlag.from(i);
                if (flag != null) {
                    flags.flags.add(flag);
                } else {
                    log.debug("Unknown Metadata flag index {} detected", i);
                }
            }
        }
        return flags;
    }

    /**
     * Set {@link EntityFlag} value
     *
     * @param flag  flag to be set
     * @param value value to be set to
     * @return whether there was a change
     */
    public boolean setFlag(EntityFlag flag, boolean value) {
        Objects.requireNonNull(flag, "flag");
        boolean contains = flags.contains(flag);
        if (contains && !value) {
            flags.remove(flag);
            return true;
        } else if (!contains && value) {
            flags.add(flag);
            return true;
        }
        return false;
    }

    /**
     * Get {@link EntityFlag} value
     *
     * @param flag flag to get
     * @return value of flag
     */
    public boolean getFlag(EntityFlag flag) {
        Objects.requireNonNull(flag, "flag");
        return flags.contains(flag);
    }

    public long get(int index) {
        long value = 0;
        final int lower = index * 64;
        final int upper = lower + 64;
        for (EntityFlag flag : flags) {
            int flagIndex = flag.getId();
            if (flagIndex >= lower && flagIndex < upper) {
                value |= 1L << (flagIndex & 0x3f);
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EntityFlags)) return false;
        EntityFlags that = (EntityFlags) o;
        return Objects.equals(this.flags, that.flags);
    }

    @Override
    public int hashCode() {
        return flags.hashCode();
    }

    public void addAll(EntityFlags flags) {
        this.flags.addAll(flags.flags);
    }

    public EntityFlags copy() {
        EntityFlags flags = new EntityFlags();
        flags.flags.addAll(this.flags);
        return flags;
    }
}
