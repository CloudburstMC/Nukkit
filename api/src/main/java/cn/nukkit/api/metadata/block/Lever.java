package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Powerable;
import cn.nukkit.api.util.data.BlockFace;
import com.google.common.base.Preconditions;

public class Lever extends Directional implements Powerable {
    private final boolean powered;

    private Lever(BlockFace face, boolean powered) {
        super(face);
        this.powered = powered;
    }

    public static Lever of(BlockFace face, boolean powered) {
        Preconditions.checkArgument(face != null, "BlockFace cannot be null");
        return new Lever(face, powered);
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lever that = (Lever) o;
        return this.powered == that.powered && this.getFace() == that.getFace();
    }

    @Override
    public int hashCode() {
        return 31 * (powered ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Lever(" +
                "powered=" + powered +
                ", face=" + getFace() +
                ')';
    }
}
