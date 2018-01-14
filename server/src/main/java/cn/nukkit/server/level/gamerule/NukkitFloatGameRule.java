package cn.nukkit.server.level.gamerule;

import cn.nukkit.api.level.gamerule.FloatGameRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class NukkitFloatGameRule implements FloatGameRule {
    private final String name;
    private float value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public float getPrimitiveValue() {
        return value;
    }
}
