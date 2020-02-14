package cn.nukkit.block;

import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparent implements Faceable {

    public static final int TRAPDOOR_OPEN_BIT = 0x08;
    public static final int TRAPDOOR_TOP_BIT = 0x04;

    public BlockTrapdoor(Identifier id) {
        super(id);
    }

    static {
        for (int damage = 0; damage < 16; damage++) {
            AxisAlignedBB bb;
            float f = 0.1875f;
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
            boundingBoxDamage[damage] = bb;
        }
    }

    @Override
    public float getHardness() {
        return 3;
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

    @Override
    public float getResistance() {
        return 15;
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        return boundingBoxDamage[this.getMeta()];
    }

    @Override
    public float getMinX() {
        return this.getX() + getRelativeBoundingBox().getMinX();
    }

    @Override
    public float getMaxX() {
        return this.getX() + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public float getMinY() {
        return this.getY() + getRelativeBoundingBox().getMinY();
    }

    @Override
    public float getMaxY() {
        return this.getY() + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public float getMinZ() {
        return this.getZ() + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + getRelativeBoundingBox().getMaxZ();
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if ((!isOpen() && this.level.isBlockPowered(this.getPosition())) || (isOpen() && !this.level.isBlockPowered(this.getPosition()))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.setDamage(this.getMeta() ^ 0x04);
                this.level.setBlock(this.getPosition(), this, true);
                this.level.addSound(this.getPosition(), isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        BlockFace facing;
        boolean top;
        int meta = 0;

        if (face.getAxis().isHorizontal() || player == null) {
            facing = face;
            top = clickPos.getY() > 0.5f;
        } else {
            facing = player.getDirection().getOpposite();
            top = face != BlockFace.UP;
        }

        int[] faces = {2, 1, 3, 0};
        int faceBit = faces[facing.getHorizontalIndex()];
        meta |= faceBit;

        if (top) {
            meta |= TRAPDOOR_TOP_BIT;
        }
        this.setDamage(meta);
        this.getLevel().setBlock(block.getPosition(), this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if(toggle(player)) {
            this.level.addSound(this.getPosition(), isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
            return true;
        }
        return false;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent ev = new DoorToggleEvent(this, player);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.setDamage(this.getMeta() ^ TRAPDOOR_OPEN_BIT);
        getLevel().setBlock(this.getPosition(), this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean isOpen() {
        return (this.getMeta() & TRAPDOOR_OPEN_BIT) != 0;
    }

    public boolean isTop() {
        return (this.getMeta() & TRAPDOOR_TOP_BIT) != 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
