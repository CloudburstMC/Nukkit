package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.sound.DoorSound;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class Trapdoor extends Transparent {

    public Trapdoor() {
        this(0);
    }

    public Trapdoor(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return TRAPDOOR;
    }


    @Override
    public String getName() {
        return "Wooden Trapdoor";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        int damage = this.getDamage();
        AxisAlignedBB bb;
        double f = 0.1875;
        if ((damage & 0x08) > 0) {
            bb = new AxisAlignedBB(
                    this.x,
                    this.y + 1 - f,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else {
            bb = new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + f,
                    this.z + 1
            );
        }
        if ((damage & 0x04) > 0) {
            if ((damage & 0x03) == 0) {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z + 1 - f,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            } else if ((damage & 0x03) == 1) {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + f
                );
            }
            if ((damage & 0x03) == 2) {
                bb.setBounds(
                        this.x + 1 - f,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            }
            if ((damage & 0x03) == 3) {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + f,
                        this.y + 1,
                        this.z + 1
                );
            }
        }
        return bb;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            int[] faces = {
                    0,
                    1,
                    2,
                    3,
                    4,
                    5
            };
            this.meta = faces[face] & 0x03;
            if (fy > 0.5) {
                this.meta |= 0x08;
            }
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.meta |= 0x04;
        this.getLevel().setBlock(this, this, true);
        this.level.addSound(new DoorSound(this));
        return true;
    }
}
