package cn.nukkit.api.metadata.blockentity;

import javax.annotation.Nonnull;

public interface SkullBlockEntity extends BlockEntity {

    int getRotation();

    void setRotation(int rotation);

    @Nonnull
    SkullType getSkullType();

    void setSkullType(@Nonnull SkullType skullType);

    enum SkullType {
        SKELETON,
        WITHER,
        ZOMBIE,
        HUMAN,
        CREEPER,
        DRAGON
    }
}
