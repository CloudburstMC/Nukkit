package cn.nukkit.entity.misc;

import cn.nukkit.entity.Entity;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public interface Tnt extends Entity {

    @Nullable
    default Entity getSource() {
        return getOwner();
    }

    default void setSource(Entity entity) {
        setOwner(entity);
    }

    @Nonnegative
    int getFuse();

    void setFuse(@Nonnegative int fuse);
}
