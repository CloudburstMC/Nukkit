package cn.nukkit.server.level.gamerule;

import cn.nukkit.api.level.gamerule.BooleanGameRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class NukkitBooleanGameRule implements BooleanGameRule {
    private final String name;
    private boolean value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {

    }

    @Override
    public boolean getPrimitiveValue() {
        return value;
    }
}
