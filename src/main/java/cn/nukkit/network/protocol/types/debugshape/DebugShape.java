package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;

@Getter
@Data
@AllArgsConstructor
public class DebugShape {

    private long id;
    /**
     * @since v859
     */
    @Nullable
    private Integer dimension;
    @Nullable
    private Vector3f position;
    @Nullable
    private Float scale;
    @Nullable
    private Vector3f rotation;
    @Nullable
    private Float totalTimeLeft;
    @Nullable
    private Color color;
    @Nullable
    private Long attachedToEntityId;
    @Nullable
    private Float maximumRenderDistance;

    public DebugShape() {
    }

    public DebugShape(long id) {
        this.id = id;
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
