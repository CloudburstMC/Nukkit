package cn.nukkit.api.level.gamerule;

public interface BooleanGameRule extends GameRule<Boolean> {
    boolean getPrimitiveValue();

    void setValue(boolean value);
}
