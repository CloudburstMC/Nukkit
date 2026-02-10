package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.annotation.Nullable;
import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebugText extends DebugShape {

    String text;

    public DebugText(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, String text, @Nullable Long attachedToEntityId) {
        super(id, dimension, position, scale, rotation, totalTimeLeft, color, attachedToEntityId);
        this.text = text;
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
