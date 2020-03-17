package cn.nukkit.block;

import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockRedstoneWire extends FloodableBlock {

    private boolean canProvidePower = true;
    private final Set<Vector3f> blocksNeedingUpdate = new HashSet<>();

    public BlockRedstoneWire(Identifier id) {
        super(id);
    }

    protected static boolean canConnectUpwardsTo(Level level, Vector3i pos) {
        return canConnectUpwardsTo(level.getBlock(pos));
    }

    protected static boolean canConnectTo(Block block, BlockFace side) {
        if (block.getId() == REDSTONE_WIRE) {
            return true;
        } else if (BlockRedstoneDiode.isDiode(block)) {
            BlockFace face = ((BlockRedstoneDiode) block).getFacing();
            return face == side || face.getOpposite() == side;
        } else {
            return block.isPowerSource() && side != null;
        }
    }

    private void updateSurroundingRedstone(boolean force) {
        this.calculateCurrentChanges(force);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (face != BlockFace.UP || !canBePlacedOn(target.getPosition())) {
            return false;
        }

        this.getLevel().setBlock(block.getPosition(), this, true, false);
        this.updateSurroundingRedstone(true);
        Vector3i pos = this.getPosition();

        for (BlockFace blockFace : Plane.VERTICAL) {
            this.level.updateAroundRedstone(blockFace.getOffset(pos), blockFace.getOpposite());
        }

        for (BlockFace blockFace : Plane.VERTICAL) {
            this.updateAround(blockFace.getOffset(pos), blockFace.getOpposite());
        }

        for (BlockFace blockFace : Plane.HORIZONTAL) {
            Vector3i v = blockFace.getOffset(pos);

            if (this.level.getBlock(v).isNormalBlock()) {
                this.updateAround(v.up(), BlockFace.DOWN);
            } else {
                this.updateAround(v.down(), BlockFace.UP);
            }
        }
        return true;
    }

    private void calculateCurrentChanges(boolean force) {
        Vector3i pos = this.getPosition();

        int meta = this.getMeta();
        int maxStrength = meta;
        this.canProvidePower = false;
        int power = this.getIndirectPower();

        this.canProvidePower = true;

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int strength = 0;

        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3i v = face.getOffset(pos);

            if (v.getX() == this.getX() && v.getZ() == this.getZ()) {
                continue;
            }


            strength = this.getMaxCurrentStrength(v, strength);

            boolean vNormal = this.level.getBlock(v).isNormalBlock();

            if (vNormal && !this.level.getBlock(pos.up()).isNormalBlock()) {
                strength = this.getMaxCurrentStrength(v.up(), strength);
            } else if (!vNormal) {
                strength = this.getMaxCurrentStrength(v.down(), strength);
            }
        }

        if (strength > maxStrength) {
            maxStrength = strength - 1;
        } else if (maxStrength > 0) {
            --maxStrength;
        } else {
            maxStrength = 0;
        }

        if (power > maxStrength - 1) {
            maxStrength = power;
        } else if (power < maxStrength && strength <= maxStrength) {
            maxStrength = Math.max(power, strength - 1);
        }

        if (meta != maxStrength) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, meta, maxStrength));

            this.setMeta(maxStrength);
            this.level.setBlock(this.getPosition(), this, false, false);

            this.level.updateAroundRedstone(this.getPosition(), null);
            for (BlockFace face : BlockFace.values()) {
                this.level.updateAroundRedstone(face.getOffset(pos), face.getOpposite());
            }
        } else if (force) {
            for (BlockFace face : BlockFace.values()) {
                this.level.updateAroundRedstone(face.getOffset(pos), face.getOpposite());
            }
        }
    }

    private int getMaxCurrentStrength(Vector3i pos, int maxStrength) {
        if (this.level.getBlockId(pos.getX(), pos.getY(), pos.getZ()) != this.getId()) {
            return maxStrength;
        } else {
            int strength = this.level.getBlockDataAt(pos.getX(), pos.getY(), pos.getZ());
            return strength > maxStrength ? strength : maxStrength;
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);

        Vector3i pos = this.getPosition();

        this.updateSurroundingRedstone(false);

        for (BlockFace blockFace : BlockFace.values()) {
            this.level.updateAroundRedstone(blockFace.getOffset(pos), null);
        }

        for (BlockFace blockFace : Plane.HORIZONTAL) {
            Vector3i v = blockFace.getOffset(pos);

            if (this.level.getBlock(v).isNormalBlock()) {
                this.updateAround(v.up(), BlockFace.DOWN);
            } else {
                this.updateAround(v.down(), BlockFace.UP);
            }
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    private void updateAround(Vector3i pos, BlockFace face) {
        if (this.level.getBlock(pos).getId() == REDSTONE_WIRE) {
            this.level.updateAroundRedstone(pos, face);

            for (BlockFace side : BlockFace.values()) {
                this.level.updateAroundRedstone(side.getOffset(pos), side.getOpposite());
            }
        }
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.REDSTONE);
    }

    public int getStrongPower(BlockFace side) {
        return !this.canProvidePower ? 0 : getWeakPower(side);
    }

    public int getWeakPower(BlockFace side) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            int power = this.getMeta();

            if (power == 0) {
                return 0;
            } else if (side == BlockFace.UP) {
                return power;
            } else {
                EnumSet<BlockFace> enumset = EnumSet.noneOf(BlockFace.class);

                for (BlockFace face : Plane.HORIZONTAL) {
                    if (this.isPowerSourceAt(face)) {
                        enumset.add(face);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
                    return power;
                } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
                    return power;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0;
        }
        // Redstone event
        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL && !this.canBePlacedOn(this.getPosition().down())) {
            this.getLevel().useBreakOn(this.getPosition());
            return Level.BLOCK_UPDATE_NORMAL;
        }

        this.updateSurroundingRedstone(false);

        return Level.BLOCK_UPDATE_NORMAL;
    }

    private boolean isPowerSourceAt(BlockFace side) {
        Vector3i pos = this.getPosition();
        Vector3i v = side.getOffset(pos);
        Block block = this.level.getBlock(v);
        boolean flag = block.isNormalBlock();
        boolean flag1 = this.level.getBlock(pos.up()).isNormalBlock();
        return !flag1 && flag && canConnectUpwardsTo(this.level, v.up()) || (canConnectTo(block, side) ||
                !flag && canConnectUpwardsTo(this.level, block.getPosition().down()));
    }

    protected static boolean canConnectUpwardsTo(Block block) {
        return canConnectTo(block, null);
    }

    public boolean canBePlacedOn(Vector3i v) {
        Block b = this.level.getBlock(v);

        return b.isSolid() && !b.isTransparent() && b.getId() != GLOWSTONE;
    }

    @Override
    public boolean isPowerSource() {
        return this.canProvidePower;
    }

    private int getIndirectPower() {
        int power = 0;
        Vector3i pos = this.getPosition();

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getIndirectPower(face.getOffset(pos), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    private int getIndirectPower(Vector3i pos, BlockFace face) {
        Block block = this.level.getBlock(pos);
        if (block.getId() == REDSTONE_WIRE) {
            return 0;
        }
        return block.isNormalBlock() ? getStrongPower(face.getOffset(pos), face) : block.getWeakPower(face);
    }

    private int getStrongPower(Vector3i pos, BlockFace direction) {
        Block block = this.level.getBlock(pos);

        if (block.getId() == REDSTONE_WIRE) {
            return 0;
        }

        return block.getStrongPower(direction);
    }
}
