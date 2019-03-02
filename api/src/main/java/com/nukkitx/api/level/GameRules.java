package com.nukkitx.api.level;

import com.nukkitx.api.level.data.GameRule;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface GameRules {

    void setGameRule(GameRule rule, boolean value);

    void setGameRule(GameRule rule, int value);

    void setGameRule(GameRule rule, float value);

    boolean getBoolean(GameRule rule);

    int getInteger(GameRule rule);

    float getFloat(GameRule rule);

    boolean contains(GameRule rule);

    @Nonnull
    List<String> getRules();
}
