package cn.nukkit.level.gamerule;

public final class IntegerGameRule implements GameRule<Integer> {
    private static final Class<Integer> CLASS = Integer.class;
    private final String name;
    private final Integer defaultValue;

    private IntegerGameRule(String name, int defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public static IntegerGameRule of(String name) {
        return of(name, 0);
    }

    public static IntegerGameRule of(String name, int defaultValue) {
        return new IntegerGameRule(name, defaultValue);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<Integer> getValueClass() {
        return CLASS;
    }

    @Override
    public Integer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "IntegerGameRule(name=" + name + ")";
    }
}
