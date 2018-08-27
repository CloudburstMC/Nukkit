package com.nukkitx.server.scoreboard;

import com.google.common.base.Preconditions;
import com.nukkitx.api.scoreboard.ModifyScoreFunction;
import com.nukkitx.api.scoreboard.Objective;
import com.nukkitx.api.scoreboard.Scorer;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.OptionalInt;

@Getter
@ParametersAreNonnullByDefault
public abstract class NukkitScorer implements Scorer {
    private final Type type;
    private final long id;

    NukkitScorer(Type type, long id) {
        this.type = type;
        this.id = id;
    }

    @Nonnull
    @Override
    public OptionalInt getScoreIn(Objective objective) {
        checkObjective(objective);
        return ((NukkitObjective) objective).getScore(id);
    }

    @Override
    public void resetScoreIn(Objective objective) {
        checkObjective(objective);
        ((NukkitObjective) objective).resetScore(id);
    }

    @Override
    public void setScoreIn(Objective objective, int score) {
        checkObjective(objective);
        ((NukkitObjective) objective).setScore(id, score);
    }

    @Override
    public void modifyScoreIn(Objective objective, ModifyScoreFunction function) {
        Preconditions.checkNotNull(function, "function");
        checkObjective(objective);
        ((NukkitObjective) objective).modifyScore(id, function);
    }

    public final void writeTo(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        write(buffer);
    }

    protected abstract void write(ByteBuf buffer);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        NukkitScorer that = (NukkitScorer) o;
        return Objects.equals(this.type, that.type) && this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return "type=" + type + ", id=" + id;
    }

    private static void checkObjective(Objective objective) {
        Preconditions.checkNotNull(objective, "objective");
        Preconditions.checkArgument(objective instanceof NukkitObjective, "Invalid objective");
    }
}
