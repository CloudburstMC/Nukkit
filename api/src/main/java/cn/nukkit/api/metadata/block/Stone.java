package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.data.StoneType;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stone implements Metadata {
    private final StoneType type;

    public static Stone of(@Nonnull StoneType type) {
        Preconditions.checkNotNull(type, "type");
        return new Stone(type);
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stone that = (Stone) o;
        return this.type == that.type;
    }

    @Override
    public String toString() {
        return "Stone(" +
                "type=" + type +
                ')';
    }
}
