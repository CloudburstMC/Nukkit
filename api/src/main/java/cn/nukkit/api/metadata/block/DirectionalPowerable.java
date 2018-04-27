package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Powerable;
import cn.nukkit.api.util.data.BlockFace;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DirectionalPowerable extends Directional implements Powerable {
    private final boolean powered;

    private DirectionalPowerable(BlockFace face, boolean powered) {
        super(face);
        this.powered = powered;
    }

    public static DirectionalPowerable of(@Nonnull BlockFace face, boolean powered) {
        Preconditions.checkNotNull(face, "face");
        return new DirectionalPowerable(face, powered);
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
        DirectionalPowerable that = (DirectionalPowerable) o;
        return this.getFace() == that.getFace() && this.powered == that.powered;
    }

    @Override
    public String toString() {
        return "DirectionalPowerable(" +
                "face=" + getFace() +
                ", isPowered=" + powered +
                ')';
    }
}
