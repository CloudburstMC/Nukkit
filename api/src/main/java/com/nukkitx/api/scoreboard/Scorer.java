package com.nukkitx.api.scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.OptionalInt;

@ParametersAreNonnullByDefault
public interface Scorer {

    long getId();

    @Nonnull
    Type getType();

    @Nonnull
    OptionalInt getScoreIn(Objective objective);

    void resetScoreIn(Objective objective);

    void setScoreIn(Objective objective, int score);

    void modifyScoreIn(Objective objective, ModifyScoreFunction function);

    enum Type {
        INVALID,
        PLAYER,
        ENTITY,
        FAKE
    }
}
