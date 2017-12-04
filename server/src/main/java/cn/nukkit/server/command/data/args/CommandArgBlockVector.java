package cn.nukkit.server.command.data.args;

public class CommandArgBlockVector {

    private int x;
    private int y;
    private int z;
    private boolean xrelative;
    private boolean yrelative;
    private boolean zrelative;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isXrelative() {
        return xrelative;
    }

    public boolean isYrelative() {
        return yrelative;
    }

    public boolean isZrelative() {
        return zrelative;
    }
}
