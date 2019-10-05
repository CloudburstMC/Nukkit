package cn.nukkit.level.gamerule;

public final class BooleanGameRule implements GameRule<Boolean> {
    private static final Class<Boolean> CLASS = Boolean.class;
    private final String name;
    private final Boolean defaultValue;

    private BooleanGameRule(String name, boolean defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public static BooleanGameRule of(String name) {
        return of(name, false);
    }

    public static BooleanGameRule of(String name, boolean defaultValue) {
        return new BooleanGameRule(name, defaultValue);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<Boolean> getValueClass() {
        return CLASS;
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "BooleanGameRule(name=" + name + ")";
    }
}
