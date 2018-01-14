package cn.nukkit.server.entity;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PlayerAttribute extends EntityAttribute {
    private final float defaultValue;

    public PlayerAttribute(String name, float minimumValue, float maximumValue, float value, float defaultValue) {
        super(name, minimumValue, maximumValue, value);
        this.defaultValue = defaultValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, minimumValue, maximumValue, value, defaultValue);
    }

    @Override
    public String toString() {
        return "PlayerAttribute{" +
                "name='" + name + '\'' +
                ", minimumValue=" + minimumValue +
                ", maximumValue=" + maximumValue +
                ", value=" + value +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
