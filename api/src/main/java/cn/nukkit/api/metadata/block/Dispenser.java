package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Powerable;
import cn.nukkit.api.util.data.BlockFace;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Dispenser extends Directional implements Powerable {
    private final boolean powered;

    private Dispenser(BlockFace face, boolean powered) {
        super(face);
        this.powered = powered;
    }

    public static Dispenser of(@Nonnull BlockFace face, boolean powered) {
        Preconditions.checkNotNull(face, "face");
        return new Dispenser(face, powered);
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFace(), powered);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dispenser that = (Dispenser) o;
        return this.getFace() == that.getFace() && this.powered == that.powered;
    }

    @Override
    public String toString() {
        return "Dispenser(" +
                "face=" + getFace() +
                ", isPowered=" + powered +
                ')';
    }
}
