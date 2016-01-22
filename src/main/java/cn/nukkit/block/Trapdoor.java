package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.sound.DoorSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Doors;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class Trapdoor extends Transparent {

    public Trapdoor() {
        this(0);
    }

    public Trapdoor(int meta) {
        super(meta);
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
        if (target.isTransparent()) return false;
        int faceBit = 0b00;
        int upDownBit = 0b000;
        if (fy > 0.5) upDownBit = 0b100;
        switch (face) {
            case Vector3.SIDE_NORTH:
                faceBit = 0b11;
                break;
            case Vector3.SIDE_SOUTH:
                faceBit = 0b10;
                break;
            case Vector3.SIDE_WEST:
                faceBit = 0b01;
                break;
            case Vector3.SIDE_EAST:
                faceBit = 0b00;
                break;
            //todo correct the faceBit according to player yaw when side is up or down
            case Vector3.SIDE_UP:
                upDownBit = 0b000;
                break;
            case Vector3.SIDE_DOWN:
                upDownBit = 0b100;
                break;
        }
        this.meta |= upDownBit;
        this.meta |= faceBit;
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!Doors.toggleOpenState(this, player)) return false;
        this.getLevel().setBlock(this, this, true);
        this.level.addSound(new DoorSound(this));
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

}
