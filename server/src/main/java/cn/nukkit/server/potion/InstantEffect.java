package cn.nukkit.server.potion;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InstantEffect extends Effect {
    public InstantEffect(int id, String name, int r, int g, int b) {
        super(id, name, r, g, b);
    }

    public InstantEffect(int id, String name, int r, int g, int b, boolean isBad) {
        super(id, name, r, g, b, isBad);
    }
}
