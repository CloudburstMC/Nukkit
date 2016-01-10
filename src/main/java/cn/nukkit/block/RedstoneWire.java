package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.redstone.Redstone;
import cn.nukkit.utils.RGBColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneWire extends Flowable {

    public RedstoneWire() {
        this(0);
    }

    public RedstoneWire(int meta) {
        super(meta);
        this.powerLevel = meta;
    }

    @Override
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    public int getId() {
        return Block.REDSTONE_WIRE;
    }

    @Override
    public void setPowerLevel(int redstonePower) {
        this.powerLevel = redstonePower;
        this.meta = redstonePower;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(Vector3.SIDE_DOWN).isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (this.getSide(Vector3.SIDE_DOWN).isTransparent()) {
            return false;
        } else {
            this.setPowerLevel(this.getNeighborPowerLevel() - 1);
            block.getLevel().setBlock(block, this, true, true);
            Redstone.active(this);
            return true;
        }
    }

    @Override
    public boolean onBreak(Item item) {
        int level = this.getPowerLevel();
        this.getLevel().setBlock(this, new Air(), true, false);
        Redstone.deactive(this, level);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.REDSTONE, 0, 1}
        };
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.airColor;
    }
}
