package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockDoor extends BlockTransparentMeta implements Faceable {

    public static int DOOR_OPEN_BIT = 0x04;
    public static int DOOR_TOP_BIT = 0x08;
    public static int DOOR_HINGE_BIT = 0x01;
    public static int DOOR_POWERED_BIT = 0x02;

    protected BlockDoor(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    public int getFullDamage() {
        int meta;

        if(isTop()) {
            meta = this.down().getDamage();
        } else {
            meta = this.getDamage();
        }
        return (this.getId() << 5 ) + (meta & 0x07 | (isTop() ? 0x08 : 0) | (isRightHinged() ? 0x10 :0));
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        double f = 0.1875;

        AxisAlignedBB bb = new SimpleAxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 2,
                this.z + 1
        );

        int j = isTop() ? (this.down().getDamage() & 0x03) : getDamage() & 0x03;
        boolean isOpen = isOpen();
        boolean isRight = isRightHinged();

        if (j == 0) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + f
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z + 1 - f,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + f,
                        this.y + 1,
                        this.z + 1
                );
            }
        } else if (j == 1) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x + 1 - f,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + f,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + f
                );
            }
        } else if (j == 2) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z + 1 - f,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + f
                    );
                }
            } else {
                bb.setBounds(
                        this.x + 1 - f,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            }
        } else if (j == 3) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + f,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x + 1 - f,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z + 1 - f,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            }
        }

        return bb;
    }

    @PowerNukkitDifference(info = "Will drop the iron door item if the support is broken", since = "1.3.1.2-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == AIR) {
                Block up = this.up();

                if (up instanceof BlockDoor) {
                    this.getLevel().setBlock(up, Block.get(BlockID.AIR), false);
                    this.getLevel().useBreakOn(this, getToolType() == ItemTool.TYPE_PICKAXE? Item.get(ItemID.DIAMOND_PICKAXE) : null);
                }

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            if ((!isOpen() && this.level.isBlockPowered(this.getLocation())) || (isOpen() && !this.level.isBlockPowered(this.getLocation()))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));

                this.toggle(null);
                this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.y > 254) return false;
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

            this.setDamage(direction);
            this.getLevel().setBlock(block, this, true, false); //Bottom
            if (blockUp instanceof BlockLiquid && ((BlockLiquid) blockUp).usesWaterLogging()) {
                this.getLevel().setBlock(blockUp, 1, blockUp, true, false);
            }
            this.getLevel().setBlock(blockUp, Block.get(this.getId(), metaUp), true, true); //Top

            if (this.level.getServer().isRedstoneEnabled()) {
                if (!this.isOpen() && this.level.isBlockPowered(this.getLocation())) {
                    this.toggle(null);
                    metaUp |= DOOR_POWERED_BIT;
                    this.getLevel().setBlockDataAt(blockUp.getFloorX(), blockUp.getFloorY(), blockUp.getFloorZ(), metaUp);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        if (isTop(this.getDamage())) {
            Block down = this.down();
            if (down.getId() == this.getId()) {
                this.getLevel().setBlock(down, Block.get(BlockID.AIR), true);
            }
        } else {
            Block up = this.up();
            if (up.getId() == this.getId()) {
                this.getLevel().setBlock(up, Block.get(BlockID.AIR), true);
            }
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);

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

        this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
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
        if( down.up().getId() != down.getId() ) {
            return false;
        }
        down.setDamage(down.getDamage() ^ DOOR_OPEN_BIT);
        getLevel().setBlock(down, down, true, true);
        return true;
    }

    public boolean isOpen() {
        if (isTop(this.getDamage())) {
            return (this.down().getDamage() & DOOR_OPEN_BIT) > 0;
        }
        else{
            return (this.getDamage() & DOOR_OPEN_BIT) >0;
        }
    }
    public boolean isTop() {
        return isTop(this.getDamage());
    }

    public boolean isTop(int meta) {
        return (meta & DOOR_TOP_BIT) != 0;
    }

    public boolean isRightHinged() {
        if (isTop()) {
            return (this.getDamage() & DOOR_HINGE_BIT) > 0;
        }
        return (this.up().getDamage() & DOOR_HINGE_BIT) > 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
