package cn.nukkit.level;

import cn.nukkit.block.Block;

/**
 * Created on 15-10-27.
 */
public class Explosion {

    private int rays = 16; //Rays
    private Level level;
    private Position source;
    private float size;
    /**
     * @var Block[]
     */
    private Block[] affectedBlocks;
    private float stepLen = 0.3F;
    /**
     * @var Entity|Block
     */
    private Object what;

    public Explosion(Position center, float size, Object what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    /**
     * @return bool
     * @deprecated
     */
    public boolean explode() {
        if (explodeA()) {
            return explodeB();
        }
        return false;
    }

    /**
     * @return bool
     */
    public boolean explodeA() {
        // TODO
        return false;
    }

    public boolean explodeB() {
        // TODO
        return false;
    }

}
