package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparentMeta implements Faceable {

    public static final int TRAPDOOR_OPEN_BIT = 0x08;
    public static final int TRAPDOOR_TOP_BIT = 0x04;

    private static final int[] FACES = {2, 1, 3, 0};

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
        return "Oak Trapdoor";
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

    private static final AxisAlignedBB[] BOUNDING_BOX_DAMAGE = new AxisAlignedBB[16];

    static {
        for (int damage = 0; damage < 16; damage++) {
            AxisAlignedBB bb;
            double f = 0.1875;
            if ((damage & TRAPDOOR_TOP_BIT) > 0) {
                bb = new SimpleAxisAlignedBB(
                        0,
                        1 - f,
                        0,
                        1,
                        1,
                        1
                );
            } else {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        1,
                        0 + f,
                        1
                );
            }
            if ((damage & TRAPDOOR_OPEN_BIT) > 0) {
                if ((damage & 0x03) == 0) {
                    bb.setBounds(
                            0,
                            0,
                            1 - f,
                            1,
                            1,
                            1
                    );
                } else if ((damage & 0x03) == 1) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            1,
                            1,
                            0 + f
                    );
                }
                if ((damage & 0x03) == 2) {
                    bb.setBounds(
                            1 - f,
                            0,
                            0,
                            1,
                            1,
                            1
                    );
                }
                if ((damage & 0x03) == 3) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            0 + f,
                            1,
                            1
                    );
                }
            }
            BOUNDING_BOX_DAMAGE[damage] = bb;
        }
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        return BOUNDING_BOX_DAMAGE[this.getDamage()];
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
            boolean powered = this.level.isBlockPowered(this);
            if ((!isOpen() && powered) || (isOpen() && !powered)) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
                this.level.setBlock(this, this, true);
                if (this.isOpen()) {
                    this.level.addSound(this, Sound.RANDOM_DOOR_OPEN);
                } else {
                    this.level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
                }
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace facing;
        boolean top;
        int meta = 0;

        if (face.getAxis().isHorizontal() || player == null) {
            facing = face;
            top = fy > 0.5;
        } else {
            facing = player.getDirection().getOpposite();
            top = face != BlockFace.UP;
        }

        meta |= FACES[facing.getHorizontalIndex()];

        if (top) {
            meta |= TRAPDOOR_TOP_BIT;
        }

        this.setDamage(meta);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return toggle(player);
    }

    public boolean toggle(Player player) {
        DoorToggleEvent ev = new DoorToggleEvent(this, player);
        level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
        level.setBlock(this, this, true, true);
        if (this.isOpen()) {
            this.level.addSound(this, Sound.RANDOM_DOOR_OPEN);
        } else {
            this.level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean isOpen() {
        return (this.getDamage() & TRAPDOOR_OPEN_BIT) != 0;
    }

    public boolean isTop() {
        return (this.getDamage() & TRAPDOOR_TOP_BIT) != 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
