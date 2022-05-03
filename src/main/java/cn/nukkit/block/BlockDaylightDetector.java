package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDaylightDetector;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetector extends BlockTransparentMeta {

    public BlockDaylightDetector() {}

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR;
    }

    @Override
    public String getName() {
        return "Daylight Detector";
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if(super.place(item, block, target, face, fx, fy, fz, player)) {
            if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
                updatePower();
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.DAYLIGHT_DETECTOR);
                new BlockEntityDaylightDetector(getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }
            return true;
        } else return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockDaylightDetectorInverted block = new BlockDaylightDetectorInverted();
        getLevel().setBlock(this, block, true, true);
        block.updatePower();
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (super.onBreak(item)) {
            if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
                getLevel().updateAroundRedstone(new Vector3(x, y, z), null);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isInverted() {
        return false;
    }

    public void updatePower() {
        int i;
        if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            i = getLevel().getBlockSkyLightAt((int) x, (int) y, (int) z) - getLevel().calculateSkylightSubtracted(1.0F);
            float f = getLevel().getCelestialAngle(1.0F);

            if (this.isInverted()) {
                i = 15 - i;
            }

            if (i > 0 && !this.isInverted()) {
                float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
                f = f + (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15);
        } else i = 0;

        if (i != getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ())) {
            getLevel().setBlockDataAt(getFloorX(), getFloorY(), getFloorZ(), i);
            getLevel().updateAroundRedstone(this, null);
        }
    }

}