package cn.nukkit.server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class EntityAttribute {
    protected final String name;
    protected final float minimumValue;
    protected final float maximumValue;
    protected final float value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityAttribute attribute = (EntityAttribute) o;
        return Objects.equals(name, attribute.name) &&
                Float.compare(attribute.minimumValue, minimumValue) == 0 &&
                Float.compare(attribute.maximumValue, maximumValue) == 0 &&
                Float.compare(attribute.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, minimumValue, maximumValue, value);
    }

    @Override
    public String toString() {
        return "PlayerAttribute{" +
                "name='" + name + '\'' +
                ", minimumValue=" + minimumValue +
                ", maximumValue=" + maximumValue +
                ", value=" + value +
                '}';
    }
}
