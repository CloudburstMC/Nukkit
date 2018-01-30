package cn.nukkit.api.level;

import cn.nukkit.api.util.data.GameRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GameRules {

    void setGameRule(@Nonnull GameRule gameRule, boolean value);

    void setGameRule(@Nonnull GameRule gameRule, int value);

    void setGameRule(@Nonnull GameRule gameRule, float value);

    boolean getBoolean(@Nonnull GameRule gameRule);

    int getInteger(@Nonnull GameRule gameRule);

    float getFloat(@Nonnull GameRule gameRule);

    @Nonnull
    String getString(@Nonnull GameRule gameRule);

    boolean contains(@Nullable GameRule gameRule);
}
