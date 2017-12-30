package cn.nukkit.api.level.gamerule;

public interface IntGameRule extends GameRule<Integer> {

    int getPrimitiveValue();

    void setValue(int value);
}
