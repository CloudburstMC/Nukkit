package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public abstract class BlockRedstoneDiode extends BlockFlowable implements RedstoneComponent, Faceable {

    protected boolean isPowered = false;

    public BlockRedstoneDiode() {
        this(0);
    }

    public BlockRedstoneDiode(int meta) {
        super(meta);
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Vector3 pos = getLocation();
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.level.getServer().isRedstoneEnabled()) {
            updateAllAroundRedstone();
        }
        return true;
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) {
            return false;
        }

        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        if (!this.level.setBlock(block, this, true, true)) {
            return false;
        }

        if (this.level.getServer().isRedstoneEnabled()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1);
            }
        }
        return true;
    }
    
    protected boolean isSupportValid(Block support) {
        return BlockLever.isSupportValid(support, BlockFace.UP) || support instanceof BlockCauldron;
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            if (!this.isLocked()) {
                Vector3 pos = getLocation();
                boolean shouldBePowered = this.shouldBePowered();

                if (this.isPowered && !shouldBePowered) {
                    this.level.setBlock(pos, this.getUnpowered(), true, true);

                    Block side = this.getSide(getFacing().getOpposite());
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(side);
                } else if (!this.isPowered) {
                    this.level.setBlock(pos, this.getPowered(), true, true);
                    Block side = this.getSide(getFacing().getOpposite());
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(side);

                    if (!shouldBePowered) {
                        level.scheduleUpdate(getPowered(), this, this.getDelay());
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid(down())) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            } else if (this.level.getServer().isRedstoneEnabled()) {
                // Redstone event
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }

                this.updateState();
                return type;
            }
        }
        return 0;
    }

    public void updateState() {
        if (!this.isLocked()) {
            boolean shouldPowered = this.shouldBePowered();

            if ((this.isPowered && !shouldPowered || !this.isPowered && shouldPowered) && !this.level.isBlockTickPending(this, this)) {
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
            return Math.max(power, block.getId() == Block.REDSTONE_WIRE ? block.getDamage() : 0);
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
        return isAlternateInput(block) ? (block.getId() == Block.REDSTONE_BLOCK ? 15 : (block.getId() == Block.REDSTONE_WIRE ? block.getDamage() : this.level.getStrongPower(pos, side))) : 0;
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
    public double getMaxY() {
        return this.y + 0.125;
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

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.125, this.z + 1);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
