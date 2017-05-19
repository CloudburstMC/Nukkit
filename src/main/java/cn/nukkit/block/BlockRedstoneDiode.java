package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

/**
 * @author CreeperFace
 */
public abstract class BlockRedstoneDiode extends BlockFlowable {

    protected boolean isPowered = false;

    public BlockRedstoneDiode() {
        this(0);
    }

    public BlockRedstoneDiode(int meta) {
        super(meta);
    }

    @Override
    public boolean onBreak(Item item) {
        Vector3 pos = getLocation();
        this.level.setBlock(this, new BlockAir(), true, true);

        for (BlockFace face : BlockFace.values()) {
            this.level.updateAroundRedstone(pos.getSide(face), null);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.getSide(BlockFace.DOWN).isTransparent()) {
            return false;
        }

        this.meta = (player.getDirection().getHorizontalIndex() + 5) % 4;
        this.level.setBlock(block, this, true, true);

        if (shouldBePowered()) {
            this.level.scheduleUpdate(this, 1);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.isLocked()) {
                Vector3 pos = getLocation();
                boolean shouldBePowered = this.shouldBePowered();

                if (this.isPowered && !shouldBePowered) {
                    this.level.setBlock(pos, this.getUnpowered(), true, true);

                    this.level.updateAroundRedstone(this.getLocation().getSide(getFacing().getOpposite()), null);
                } else if (!this.isPowered) {
                    this.level.setBlock(pos, this.getPowered(), true, true);
                    this.level.updateAroundRedstone(this.getLocation().getSide(getFacing().getOpposite()), null);

                    if (!shouldBePowered) {
                        level.scheduleUpdate(getPowered(), this, this.getDelay());
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (type == Level.BLOCK_UPDATE_NORMAL && this.getSide(BlockFace.DOWN).isTransparent()) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            } else {
                this.updateState();
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    public void updateState() {
        if (!this.isLocked()) {
            boolean shouldPowered = this.shouldBePowered();

            if ((this.isPowered && !shouldPowered || !this.isPowered && shouldPowered) && !this.level.isUpdateScheduled(this, this)) {
                /*int priority = -1;

                if (this.isFacingTowardsRepeater()) {
                    priority = -3;
                } else if (this.isPowered) {
                    priority = -2;
                }*/

                this.level.scheduleUpdate(this, this, this.getDelay());
            }
        }
    }

    public boolean isLocked() {
        return false;
    }

    protected int calculateInputStrength() {
        BlockFace face = getFacing();
        Vector3 pos = this.getLocation().getSide(face);
        int power = this.level.getRedstonePower(pos, face);

        if (power >= 15) {
            return power;
        } else {
            Block block = this.level.getBlock(pos);
            return Math.max(power, block.getId() == Block.REDSTONE_WIRE ? block.meta : 0);
        }
    }

    protected int getPowerOnSides() {
        Vector3 pos = getLocation();

        BlockFace face = getFacing();
        BlockFace face1 = face.rotateY();
        BlockFace face2 = face.rotateYCCW();
        return Math.max(this.getPowerOnSide(pos.getSide(face1), face1), this.getPowerOnSide(pos.getSide(face2), face2));
    }

    protected int getPowerOnSide(Vector3 pos, BlockFace side) {
        Block block = this.level.getBlock(pos);
        return isAlternateInput(block) ? (block.getId() == Block.REDSTONE_BLOCK ? 15 : (block.getId() == Block.REDSTONE_WIRE ? block.meta : this.level.getStrongPower(pos, side))) : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    protected boolean shouldBePowered() {
        return this.calculateInputStrength() > 0;
    }

    public abstract BlockFace getFacing();

    protected abstract int getDelay();

    protected abstract Block getUnpowered();

    protected abstract Block getPowered();

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.125, this.z + 1);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    protected boolean isAlternateInput(Block block) {
        return block.isPowerSource();
    }

    public static boolean isDiode(Block block) {
        return block instanceof BlockRedstoneDiode;
    }

    protected int getRedstoneSignal() {
        return 15;
    }

    public int getStrongPower(BlockFace side) {
        return getWeakPower(side);
    }

    public int getWeakPower(BlockFace side) {
        return !this.isPowered() ? 0 : (getFacing() == side ? this.getRedstoneSignal() : 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public boolean isFacingTowardsRepeater() {
        BlockFace side = getFacing().getOpposite();
        Block block = this.getSide(side);
        return block instanceof BlockRedstoneDiode && ((BlockRedstoneDiode) block).getFacing() != side;
    }
}