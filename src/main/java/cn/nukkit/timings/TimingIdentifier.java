package cn.nukkit.timings;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Tee7even
 */
class TimingIdentifier {
    static final Map<String, TimingGroup> GROUP_MAP = new IdentityHashMap<>(64);
    static final TimingGroup DEFAULT_GROUP = getGroup("Nukkit");

    final String group;
    final String name;
    final Timing groupTiming;
    private final int hashCode;

    TimingIdentifier(String group, String name, Timing groupTiming) {
        this.group = group != null ? group.intern() : DEFAULT_GROUP.name;
        this.name = name.intern();
        this.groupTiming = groupTiming;
        this.hashCode = (31 * this.group.hashCode()) + this.name.hashCode();
    }

    static TimingGroup getGroup(String name) {
        if (name == null) {
            return DEFAULT_GROUP;
        }

        return GROUP_MAP.computeIfAbsent(name, k -> new TimingGroup(name));
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TimingIdentifier)) {
            return false;
        }

        TimingIdentifier that = (TimingIdentifier) o;
        //Using intern() method on strings makes possible faster string comparison with ==
        return this.group == that.group && this.name == that.name;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    static class TimingGroup {
        private static int idPool = 1;
        final int id = idPool++;

        final String name;
        ArrayDeque<Timing> timings = new ArrayDeque<>(64);

        TimingGroup(String name) {
            this.name = name.intern();
        }
    }
}
