package cn.nukkit.nbt.stream;

public class PGZIPThreadLocal extends ThreadLocal<PGZIPState> {

    private final PGZIPOutputStream parent;

    public PGZIPThreadLocal(final PGZIPOutputStream parent) {
        this.parent = parent;
    }

    @Override
    protected PGZIPState initialValue() {
        return new PGZIPState(this.parent);
    }

}
