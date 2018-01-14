package cn.nukkit.server.nbt.tag;

public class EndTag extends Tag<Void> {
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {
        super(null);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Void getValue() {
        return null;
    }
}
