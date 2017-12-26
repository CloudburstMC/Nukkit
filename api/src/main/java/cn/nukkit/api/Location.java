package cn.nukkit.api;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.util.Rotation;
import com.flowpowered.math.vector.Vector3f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location extends Vector3f {

    private Level level;
    private Rotation rotation;

    public Location() {

    }

    public Location(Level level, Vector3f pos) {
        this(level, pos.getX(), pos.getY(), pos.getZ());
    }

    public Location(Level level, float x, float y, float z) {
        this(level, x, y, z, 0, 0);
    }

    public Location(Level level, float x, float y, float z, float pitch, float yaw) {
        this(level, x, y, z, pitch, yaw, 0);
    }

    public Location(Level level, float x, float y, float z, float pitch, float yaw, float headYaw) {
        super(x, y, z);
        this.level = level;
        this.rotation = new Rotation(pitch, yaw, headYaw);
    }

    public float getYaw() {
        return rotation.getYaw();
    }

    public float getPitch() {
        return rotation.getPitch();
    }

    public float getHeadYaw() {
        return rotation.getHeadYaw();
    }
}
