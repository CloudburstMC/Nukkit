package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;
import cn.nukkit.utils.TextFormat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bed extends Transparent {

    public Bed() {
        this(0);
    }

    public Bed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BED_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public String getName() {
        return "Bed Block";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.5625,
                this.z + 1
        );
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        int time = this.getLevel().getTime() % Level.TIME_FULL;

        boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);

        if (player != null && !isNight) {
            player.sendMessage(TextFormat.GRAY + "You can only sleep at night");
            return true;
        }

        Block blockNorth = this.getSide(2);
        Block blockSouth = this.getSide(3);
        Block blockEast = this.getSide(5);
        Block blockWest = this.getSide(4);

        Block b;
        if ((this.meta & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == this.getId() && (blockNorth.meta & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == this.getId() && (blockSouth.meta & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == this.getId() && (blockEast.meta & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == this.getId() && (blockWest.meta & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage(TextFormat.GRAY + "This bed is incomplete");
                }

                return true;
            }
        }

        if (player != null && !player.sleepOn(b)) {
            player.sendMessage(TextFormat.GRAY + "This bed is occupied");
        }


        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (!down.isTransparent()) {
            int[] faces = {3, 4, 2, 5};
            int d = player != null ? player.getDirection() : 0;
            Block next = this.getSide(faces[((d + 3) % 4)]);
            Block downNext = this.getSide(0);

            if (next.canBeReplaced() && !downNext.isTransparent()) {
                int meta = ((d + 3) % 4) & 0x03;
                this.getLevel().setBlock(block, Block.get(this.getId(), meta), true, true);
                this.getLevel().setBlock(next, Block.get(this.getId(), meta | 0x08), true, true);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block blockNorth = this.getSide(2); //Gets the blocks around them
        Block blockSouth = this.getSide(3);
        Block blockEast = this.getSide(5);
        Block blockWest = this.getSide(4);

        if ((this.meta & 0x08) == 0x08) { //This is the Top part of bed
            if (blockNorth.getId() == this.getId() && blockNorth.meta != 0x08) { //Checks if the block ID&&meta are right
                this.getLevel().setBlock(blockNorth, new Air(), true, true);
            } else if (blockSouth.getId() == this.getId() && blockSouth.meta != 0x08) {
                this.getLevel().setBlock(blockSouth, new Air(), true, true);
            } else if (blockEast.getId() == this.getId() && blockEast.meta != 0x08) {
                this.getLevel().setBlock(blockEast, new Air(), true, true);
            } else if (blockWest.getId() == this.getId() && blockWest.meta != 0x08) {
                this.getLevel().setBlock(blockWest, new Air(), true, true);
            }
        } else { //Bottom Part of Bed
            if (blockNorth.getId() == this.getId() && (blockNorth.meta & 0x08) == 0x08) {
                this.getLevel().setBlock(blockNorth, new Air(), true, true);
            } else if (blockSouth.getId() == this.getId() && (blockSouth.meta & 0x08) == 0x08) {
                this.getLevel().setBlock(blockSouth, new Air(), true, true);
            } else if (blockEast.getId() == this.getId() && (blockEast.meta & 0x08) == 0x08) {
                this.getLevel().setBlock(blockEast, new Air(), true, true);
            } else if (blockWest.getId() == this.getId() && (blockWest.meta & 0x08) == 0x08) {
                this.getLevel().setBlock(blockWest, new Air(), true, true);
            }
        }
        this.getLevel().setBlock(this, new Air(), true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.BED, 0, 1}
        };
    }

    @Override
    public Color getMapColor() {
        return Color.clothColor;
    }
}
