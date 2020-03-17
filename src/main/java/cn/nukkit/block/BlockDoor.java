package cn.nukkit.block;

import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockDoor extends BlockTransparent implements Faceable {

    public static int DOOR_OPEN_BIT = 0x04;
    public static int DOOR_TOP_BIT = 0x08;
    public static int DOOR_HINGE_BIT = 0x01;
    public static int DOOR_POWERED_BIT = 0x02;

    public BlockDoor(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        float f = 0.1875f;

        AxisAlignedBB bb = new SimpleAxisAlignedBB(
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getX() + 1,
                this.getY() + 2,
                this.getZ() + 1
        );

        int j = isTop() ? (this.down().getMeta() & 0x03) : getMeta() & 0x03;
        boolean isOpen = isOpen();
        boolean isRight = isRightHinged();

        if (j == 0) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + f
                    );
                } else {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ() + 1 - f,
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        this.getX() + f,
                        this.getY() + 1,
                        this.getZ() + 1
                );
            }
        } else if (j == 1) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.getX() + 1 - f,
                            this.getY(),
                            this.getZ(),
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                } else {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            this.getX() + f,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        this.getX() + 1,
                        this.getY() + 1,
                        this.getZ() + f
                );
            }
        } else if (j == 2) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ() + 1 - f,
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                } else {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + f
                    );
                }
            } else {
                bb.setBounds(
                        this.getX() + 1 - f,
                        this.getY(),
                        this.getZ(),
                        this.getX() + 1,
                        this.getY() + 1,
                        this.getZ() + 1
                );
            }
        } else if (j == 3) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            this.getX() + f,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                } else {
                    bb.setBounds(
                            this.getX() + 1 - f,
                            this.getY(),
                            this.getZ(),
                            this.getX() + 1,
                            this.getY() + 1,
                            this.getZ() + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.getX(),
                        this.getY(),
                        this.getZ() + 1 - f,
                        this.getX() + 1,
                        this.getY() + 1,
                        this.getZ() + 1
                );
            }
        }

        return bb;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == AIR) {
                Block up = this.up();

                if (up instanceof BlockDoor) {
                    this.getLevel().setBlock(up.getPosition(), Block.get(AIR), false);
                    this.getLevel().useBreakOn(this.getPosition());
                }

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if ((!isOpen() && this.level.isBlockPowered(this.getPosition())) || (isOpen() && !this.level.isBlockPowered(this.getPosition()))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));

                this.toggle(null);
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (this.getY() > 254) return false;
        if (face == BlockFace.UP) {
            Block blockUp = this.up();
            Block blockDown = this.down();
            if (!blockUp.canBeReplaced() || blockDown.isTransparent()) {
                return false;
            }
            int[] faces = {1, 2, 3, 0};
            int direction = faces[player != null ? player.getDirection().getHorizontalIndex() : 0];

            Block left = this.getSide(player.getDirection().rotateYCCW());
            Block right = this.getSide(player.getDirection().rotateY());
            int metaUp = DOOR_TOP_BIT;
            if (left.getId() == this.getId() || (!right.isTransparent() && left.isTransparent())) { //Door hinge
                metaUp |= DOOR_HINGE_BIT;
            }

            this.setMeta(direction);
            this.getLevel().setBlock(block.getPosition(), this, true, false); //Bottom
            if (blockUp instanceof BlockLiquid && ((BlockLiquid) blockUp).usesWaterLogging()) {
                this.getLevel().setBlock(blockUp.getPosition(), 1, blockUp.clone(), true, false);
            }
            this.getLevel().setBlock(blockUp.getPosition(), Block.get(this.getId(), metaUp), true, true); //Top

            if (!this.isOpen() && this.level.isBlockPowered(this.getPosition())) {
                this.toggle(null);
                metaUp |= DOOR_POWERED_BIT;
                this.getLevel().setBlockDataAt(blockUp.getX(), blockUp.getY(), blockUp.getZ(), metaUp);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        if (isTop(this.getMeta())) {
            Block down = this.down();
            if (down.getId() == this.getId()) {
                down.removeBlock(true);
            }
        } else {
            Block up = this.up();
            if (up.getId() == this.getId()) {
                up.removeBlock(true);
            }
        }
        super.onBreak(item);

        return true;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!this.toggle(player)) {
            return false;
        }

        this.level.addSound(this.getPosition(), isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
        return true;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        Block down;
        if (isTop()) {
            down = this.down();
        } else {
            down = this;
        }
        if (down.up().getId() != down.getId()) {
            return false;
        }
        down.setMeta(down.getMeta() ^ DOOR_OPEN_BIT);
        getLevel().setBlock(down.getPosition(), down, true, true);
        return true;
    }

    public boolean isOpen() {
        if (isTop(this.getMeta())) {
            return (this.down().getMeta() & DOOR_OPEN_BIT) > 0;
        } else {
            return (this.getMeta() & DOOR_OPEN_BIT) > 0;
        }
    }
    public boolean isTop() {
        return isTop(this.getMeta());
    }

    public boolean isTop(int meta) {
        return (meta & DOOR_TOP_BIT) != 0;
    }

    public boolean isRightHinged() {
        if (isTop()) {
            return (this.getMeta() & DOOR_HINGE_BIT) > 0;
        }
        return (this.up().getMeta() & DOOR_HINGE_BIT) > 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
