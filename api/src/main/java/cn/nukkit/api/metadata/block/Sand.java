package cn.nukkit.api.metadata.block;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sand {
    private final boolean red;

    public boolean isRed() {
        return red;
    }

    @Override
    public int hashCode() {
        return red ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sand that = (Sand) o;
        return this.red == that.red;
    }

    @Override
    public String toString() {
        return "Sand(" +
                "isRed=" + red +
                ')';
    }
}
