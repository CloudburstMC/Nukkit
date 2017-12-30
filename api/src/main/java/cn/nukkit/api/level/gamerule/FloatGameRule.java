package cn.nukkit.api.level.gamerule;

public interface FloatGameRule extends GameRule<Float> {

    float getPrimitiveValue();

    void setValue(float value);
}
