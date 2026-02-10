package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.awt.*;

@Getter
@AllArgsConstructor
public class DebugShape {

    private final long id;

    /**
     * @since v859
     */
    private final Integer dimension;
    @Nullable
    private final Vector3f position;
    @Nullable
    private final Float scale;
    @Nullable
    private final Vector3f rotation;
    @Nullable
    private final Float totalTimeLeft;
    @Nullable
    private final Color color;
    @Nullable
    private final Long attachedToEntityId;

    public DebugShape(long id) {
        this(id, 0, null, null, null, null, null, null);
    }

    public Type getType() {
        return null;
    }

    public enum Type {
        LINE,
        BOX,
        SPHERE,
        CIRCLE,
        TEXT,
        ARROW
    }
}
