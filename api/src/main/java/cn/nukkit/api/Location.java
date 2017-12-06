package cn.nukkit.api;

import cn.nukkit.api.util.Rotation;
import com.flowpowered.math.vector.Vector3f;

public interface Location {
    Vector3f getPosition();
    Rotation getRotation();
}
