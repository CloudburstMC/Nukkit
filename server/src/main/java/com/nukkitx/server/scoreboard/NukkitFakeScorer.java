package com.nukkitx.server.scoreboard;

import com.nukkitx.api.scoreboard.FakeScorer;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

@Getter
public class NukkitFakeScorer extends NukkitScorer implements FakeScorer {
    private final String name;

    public NukkitFakeScorer(Type type, long id, String name) {
        super(type, id);
        this.name = name;
    }

    @Override
    protected void write(@Nonnull ByteBuf buffer) {
        BedrockUtil.writeString(buffer, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        NukkitFakeScorer that = (NukkitFakeScorer) o;
        return Objects.equals(this.getType(), that.getType()) && this.getId() == that.getId() &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return "NukkitFakeScorer(" + super.toString() + ", name=" + name + ')';
    }
}
