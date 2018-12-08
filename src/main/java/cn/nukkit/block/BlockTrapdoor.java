package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparentMeta {

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

    private static final AxisAlignedBB[] boundingBoxDamage = new AxisAlignedBB[16];

    static {
        for (int damage = 0; damage < 16; damage++) {
            AxisAlignedBB bb;
            double f = 0.1875;
            if ((damage & 0x08) > 0) {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0 + 1 - f,
                        0,
                        0 + 1,
                        0 + 1,
                        0 + 1
                );
            } else {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        0 + 1,
                        0 + f,
                        0 + 1
                );
            }
            if ((damage & 0x04) > 0) {
                if ((damage & 0x03) == 0) {
                    bb.setBounds(
                            0,
                            0,
                            0 + 1 - f,
                            0 + 1,
                            0 + 1,
                            0 + 1
                    );
                } else if ((damage & 0x03) == 1) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            0 + 1,
                            0 + 1,
                            0 + f
                    );
                }
                if ((damage & 0x03) == 2) {
                    bb.setBounds(
                            0 + 1 - f,
                            0,
                            0,
                            0 + 1,
                            0 + 1,
                            0 + 1
                    );
                }
                if ((damage & 0x03) == 3) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            0 + f,
                            0 + 1,
                            0 + 1
                    );
                }
            }
            boundingBoxDamage[damage] = bb;
        }
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        return boundingBoxDamage[this.getDamage()];
    }

    @Override
    public double getMinX() {
        return this.x + getRelativeBoundingBox().getMinX();
    }

    @Override
    public double getMaxX() {
        return this.x + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public double getMinY() {
        return this.y + getRelativeBoundingBox().getMinY();
    }

    @Override
    public double getMaxY() {
        return this.y + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public double getMinZ() {
        return this.z + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public double getMaxZ() {
        return this.z + getRelativeBoundingBox().getMaxZ();
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if ((!isOpen() && this.level.isBlockPowered(this.getLocation())) || (isOpen() && !this.level.isBlockPowered(this.getLocation()))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.setDamage(this.getDamage() ^ 0x08);
                this.level.setBlock(this, this, true);
                this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
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

        this.setDamage(this.getDamage() | faceBit);

        if (top) {
            this.setDamage(this.getDamage() | 0x04);
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
        this.setDamage(this.getDamage() ^ 0x08);
        this.level.setBlock(this, this, true);
        this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public BlockFace getFacing() {
        int[] faces = {3, 1, 0, 2};
        return BlockFace.fromHorizontalIndex(faces[this.getDamage() & 0x03]);
    }

    public boolean isOpen() {
        return (this.getDamage() & 0x08) != 0;
    }

    public boolean isTop() {
        return (this.getDamage() & 0x04) != 0;
    }
}
