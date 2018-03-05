package cn.nukkit.api.level.data;

public enum Generator {

    LEGACY("Legacy"),
    OVERWORLD("Overworld"),
    FLAT("Flat"),
    NETHER("Nether"),
    THE_END("The End"),
    UNDEFINED("Undefined");

    private final String name;

    Generator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
