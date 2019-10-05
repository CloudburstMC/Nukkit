package cn.nukkit.level.gamerule;

public final class FloatGameRule implements GameRule<Float> {
    private static final Class<Float> CLASS = Float.class;
    private final String name;
    private final Float defaultValue;

    private FloatGameRule(String name, float defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public static FloatGameRule of(String name) {
        return of(name, 0.0f);
    }

    public static FloatGameRule of(String name, float defaultValue) {
        return new FloatGameRule(name, defaultValue);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<Float> getValueClass() {
        return CLASS;
    }

    @Override
    public Float getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Float parse(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "FloatGameRule(name=" + name + ")";
    }
}
