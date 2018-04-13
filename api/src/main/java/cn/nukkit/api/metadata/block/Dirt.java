package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.data.DirtType;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dirt implements Metadata {
    private final DirtType type;

    public static Dirt of(@Nonnull DirtType type) {
        return new Dirt(Preconditions.checkNotNull(type, "type"));
    }

    public DirtType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }


}
