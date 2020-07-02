package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparentMeta implements Faceable {
    private static final IntList FACES = new IntArrayList(new int[]{2, 1, 3, 0});

    public static final int TRAPDOOR_OPEN_BIT = 0x08;
    public static final int TRAPDOOR_TOP_BIT = 0x04;

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

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    private static final AxisAlignedBB[] boundingBoxDamage = new AxisAlignedBB[16];

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
                BlockFace face = getFaceForDamage(damage);
                if (face == BlockFace.NORTH) {
                    bb.setBounds(
                            0,
                            0,
                            1 - f,
                            1,
                            1,
                            1
                    );
                } else if (face == BlockFace.SOUTH) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            1,
                            1,
                            0 + f
                    );
                }
                if (face == BlockFace.WEST) {
                    bb.setBounds(
                            1 - f,
                            0,
                            0,
                            1,
                            1,
                            1
                    );
                }
                if (face == BlockFace.EAST) {
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
            boundingBoxDamage[damage] = bb;
        }
    }

    @PowerNukkitDifference(info = "The bounding box was fixed", since = "1.3.0.0-PN")
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
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            if ((!isOpen() && this.level.isBlockPowered(this.getLocation())) || (isOpen() && !this.level.isBlockPowered(this.getLocation()))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
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
        int meta = 0;

        if (face.getAxis().isHorizontal() || player == null) {
            facing = face;
            top = fy > 0.5;
        } else {
            facing = player.getDirection().getOpposite();
            top = face != BlockFace.UP;
        }

        int faceBit = FACES.getInt(facing.getHorizontalIndex());
        meta |= faceBit;

        if (top) {
            meta |= TRAPDOOR_TOP_BIT;
        }
        
        this.setDamage(meta);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if(toggle(player)) {
            this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
            return true;
        }
        return false;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent ev = new DoorToggleEvent(this, player);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if(ev.isCancelled()) {
            return false;
        }
        this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
        getLevel().setBlock(this, this, true);
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

    @PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return getFaceForDamage(getDamage());
    }
    
    private static BlockFace getFaceForDamage(int damage) {
        int stairFace = damage & 0x3;
        int horizontalIndex = FACES.indexOf(stairFace);
        return BlockFace.fromHorizontalIndex(horizontalIndex);
    }
}
