package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.sound.LeverSound;
import cn.nukkit.redstone.Redstone;

/**
 * @author Nukkit Project Team
 */
public class BlockLever extends BlockFlowable {

    public BlockLever() {
        this(0);
    }

    public BlockLever(int meta) {
        super(meta);
        this.setPowerLevel(Redstone.POWER_STRONGEST);
    }

    @Override
    public String getName() {
        return "Lever";
    }

    @Override
    public int getId() {
        return LEVER;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5d;
    }

    @Override
    public double getResistance() {
        return 2.5d;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.LEVER, 0, 1}
        };
    }

    public boolean isPowerOn() {
        return (this.meta & 0x08) > 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.meta ^= 0x08;

        this.getLevel().setBlock(this, this, true, true);
        this.getLevel().addSound(new LeverSound(this, this.isPowerOn()));
        if (this.isPowerOn()) {
            this.setPowerSource(true);
            Redstone.active(this);
        } else {
            this.setPowerSource(false);
            Redstone.deactive(this, this.getPowerLevel());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int[] faces = new int[]{
                    1,
                    4,
                    5,
                    2,
                    3,
                    0,
                    0,
                    1
            };
            int face = this.isPowerOn() ? this.meta ^ 0x08 : this.meta;
            if (this.getSide(faces[face]).isTransparent()) {
                this.onBreak(null);
                for (int[] item : this.getDrops(null)) {
                    this.getLevel().dropItem(this, Item.get(item[0], item[1], item[2]));
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            int[] faces = new int[]{
                    0,
                    5,
                    4,
                    3,
                    2,
                    1
            };
            int to;

            if (face == 0) {
                to = player != null ? player.getDirection() : 0;
                this.meta = (to % 2 == 0 ? 0 : 7);
            } else if (face == 1) {
                to = player != null ? player.getDirection() : 0;
                this.meta = (to % 2 == 0 ? 6 : 5);
            } else {
                this.meta = faces[face];
            }
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        Redstone.deactive(this, this.getPowerLevel());
        return true;
    }

}
