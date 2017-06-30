package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.sound.DoorSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparent {

    public BlockTrapdoor() {
        this(0);
    }

    public BlockTrapdoor(int meta) {
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
        return ItemTool.TYPE_AXE;
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_NORMAL) {
            if ((!isOpen() && this.level.isBlockPowered(this)) || (isOpen() && !this.level.isBlockPowered(this))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.toggle(null);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace facing;
        boolean top;

        if (face.getAxis().isHorizontal() || player == null) {
            facing = face;
            top = fy > 0.5;
        } else {
            facing = player.getDirection().getOpposite();
            top = face != BlockFace.UP;
        }

        int[] faces = {2, 1, 3, 0};
        int faceBit = faces[facing.getHorizontalIndex()];

        this.meta |= faceBit;

        if (top) {
            this.meta |= 0x04;
        }

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!this.toggle(player)) {
            return false;
        }

        this.getLevel().setBlock(this, this, true);
        this.level.addSound(new DoorSound(this));
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }


        int sideBit = this.meta & 0x07;
        boolean open = isOpen();

        this.meta = sideBit;

        if (open) {
            this.meta |= 0x08;
        }

        this.level.setBlock(this, this, false, false);
        this.level.addSound(new DoorSound(this));
        return true;
    }

    public BlockFace getFacing() {
        int[] faces = {3, 1, 0, 2};
        return BlockFace.fromHorizontalIndex(faces[this.meta & 0x03]);
    }

    public boolean isOpen() {
        return (this.meta & 0x08) != 0;
    }

    public boolean isTop() {
        return (this.meta & 0x04) != 0;
    }
}
