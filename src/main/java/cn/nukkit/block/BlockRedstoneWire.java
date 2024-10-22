package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.EnumSet;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockRedstoneWire extends BlockFlowable {

    private boolean canProvidePower = true;

    public BlockRedstoneWire() {
        this(0);
    }

    public BlockRedstoneWire(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    public int getId() {
        return REDSTONE_WIRE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockWater || block.level.isBlockWaterloggedAt(block.getChunk(), (int) block.x, (int) block.y, (int) block.z)) {
            return false;
        }

        if (!canStayOnFullSolid(block.down())) {
            return false;
        }

        this.getLevel().setBlock(block, this, true, false);
        this.calculateCurrentChanges(true);

        for (BlockFace blockFace : Plane.VERTICAL) {
            this.level.updateAroundRedstone(this.getSideVec(blockFace), blockFace.getOpposite());
        }

        for (BlockFace blockFace : Plane.VERTICAL) {
            this.updateAround(this.getSideVec(blockFace), blockFace.getOpposite());
        }

        for (BlockFace blockFace : Plane.HORIZONTAL) {
            Vector3 v = this.getSideVec(blockFace);

            if (this.level.getBlock(v).isNormalBlock()) {
                this.updateAround(v.getSideVec(BlockFace.UP), BlockFace.DOWN);
            } else {
                this.updateAround(v.getSideVec(BlockFace.DOWN), BlockFace.UP);
            }
        }
        return true;
    }

    private void updateAround(Vector3 pos, BlockFace face) {
        if (this.level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.REDSTONE_WIRE) {
            this.level.updateAroundRedstone(pos, face);

            for (BlockFace side : BlockFace.values()) {
                this.level.updateAroundRedstone(pos.getSideVec(side), side.getOpposite());
            }
        }
    }

    private void calculateCurrentChanges(boolean force) {
        int meta = this.getDamage();
        int maxStrength = meta;
        this.canProvidePower = false;
        int power = this.getIndirectPower();

        this.canProvidePower = true;

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int strength = 0;

        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 v = this.getSideVec(face);

            if (v.getX() == this.getX() && v.getZ() == this.getZ()) {
                continue;
            }


            strength = this.getMaxCurrentStrength(v, strength);

            boolean vNormal = this.level.getBlock(v).isNormalBlock();

            if (vNormal && !this.level.getBlock(this.getSideVec(BlockFace.UP)).isNormalBlock()) {
                strength = this.getMaxCurrentStrength(v.getSideVec(BlockFace.UP), strength);
            } else if (!vNormal) {
                strength = this.getMaxCurrentStrength(v.getSideVec(BlockFace.DOWN), strength);
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

            this.setDamage(maxStrength);
            this.level.setBlock(this, this, false, false);

            this.level.updateAroundRedstone(this, null);
            for (BlockFace face : BlockFace.values()) {
                this.level.updateAroundRedstone(this.getSideVec(face), face.getOpposite());
            }
        } else if (force) {
            for (BlockFace face : BlockFace.values()) {
                this.level.updateAroundRedstone(this.getSideVec(face), face.getOpposite());
            }
        }
    }

    private int getMaxCurrentStrength(Vector3 pos, int maxStrength) {
        if (this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) != REDSTONE_WIRE) {
            return maxStrength;
        } else {
            int strength = this.level.getBlockDataAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
            return Math.max(strength, maxStrength);
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        this.level.updateAroundRedstone(this, null);

        for (BlockFace blockFace : BlockFace.values()) {
            this.level.updateAroundRedstone(this.getSideVec(blockFace), null);
        }

        for (BlockFace blockFace : Plane.HORIZONTAL) {
            Vector3 v = this.getSideVec(blockFace);

            if (this.level.getBlock(v).isNormalBlock()) {
                this.updateAround(v.getSideVec(BlockFace.UP), BlockFace.DOWN);
            } else {
                this.updateAround(v.getSideVec(BlockFace.DOWN), BlockFace.UP);
            }
        }
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.REDSTONE_DUST);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL && !canStayOnFullSolid(this.down())) {
            this.getLevel().useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        // Redstone event
        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        // Make sure the block still exists to prevent item duplication
        if (this.level.getBlockIdAt((int) this.x, (int) this.y, (int) this.z) != this.getId()) {
            return 0;
        }

        this.calculateCurrentChanges(false);

        return Level.BLOCK_UPDATE_REDSTONE;
    }

    public boolean canBePlacedOn(Vector3 v) {
        return canStayOnFullSolid(this.level.getBlock(v));
    }

    public int getStrongPower(BlockFace side) {
        return !this.canProvidePower ? 0 : getWeakPower(side);
    }

    public int getWeakPower(BlockFace side) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            int power = this.getDamage();

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

    private boolean isPowerSourceAt(BlockFace side) {
        Block sideBlock = this.getSide(side);
        boolean sideBlockIsNormal = sideBlock.isNormalBlock();
        return (sideBlockIsNormal && !this.up().isNormalBlock() && canConnectUpwardsTo(sideBlock.up())) ||
                (canConnectTo(sideBlock, side) || (!sideBlockIsNormal && canConnectUpwardsTo(sideBlock.down())));
    }

    protected static boolean canConnectUpwardsTo(Block block) {
        return canConnectTo(block, null);
    }

    protected static boolean canConnectTo(Block block, BlockFace side) {
        if (block.getId() == Block.REDSTONE_WIRE) {
            return true;
        } else if (BlockRedstoneDiode.isDiode(block)) {
            BlockFace face = ((BlockRedstoneDiode) block).getFacing();
            return face == side || face.getOpposite() == side;
        } else {
            return block.isPowerSource() && side != null;
        }
    }

    @Override
    public boolean isPowerSource() {
        return this.canProvidePower;
    }

    private int getIndirectPower() {
        int power = 0;

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getIndirectPower(this.getSideVec(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    private int getIndirectPower(Vector3 pos, BlockFace face) {
        Block block = this.level.getBlock(pos);
        if (block.getId() == Block.REDSTONE_WIRE) {
            return 0;
        }
        return block.isNormalBlock() ? getStrongPower(pos.getSideVec(face), face) : block.getWeakPower(face);
    }

    private int getStrongPower(Vector3 pos, BlockFace direction) {
        Block block = this.level.getBlock(pos);

        if (block.getId() == Block.REDSTONE_WIRE) {
            return 0;
        }

        return block.getStrongPower(direction);
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
