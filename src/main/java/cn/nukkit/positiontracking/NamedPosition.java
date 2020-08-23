package cn.nukkit.positiontracking;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class NamedPosition extends Vector3 {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NamedPosition() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NamedPosition(double x) {
        super(x);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NamedPosition(double x, double y) {
        super(x, y);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NamedPosition(double x, double y, double z) {
        super(x, y, z);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public abstract String getLevelName();
    
    public boolean matchesNamedPosition(NamedPosition position) {
        return x == position.x && y == position.y && z == position.z && getLevelName().equals(position.getLevelName());
    }

    @Override
    public NamedPosition clone() {
        return (NamedPosition) super.clone();
    }
}
