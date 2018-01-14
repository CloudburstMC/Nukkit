package cn.nukkit.server.level.gamerule;

import cn.nukkit.api.level.gamerule.IntGameRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class NukkitIntGameRule implements IntGameRule {
    private final String name;
    private int value;

    @Override
    public int getPrimitiveValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
}
