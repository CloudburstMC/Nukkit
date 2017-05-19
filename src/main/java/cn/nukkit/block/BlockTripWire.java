package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BlockTripWire extends BlockFlowable {

    public BlockTripWire(int meta) {
        super(meta);
    }

    public BlockTripWire() {
        this(0);
    }

    @Override
    public int getId() {
        return TRIPWIRE;
    }

    @Override
    public String getName() {
        return "Tripwire";
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.STRING, 0, 1}};
    }

    public boolean isPowered() {
        return (this.meta & 1) > 0;
    }

    public boolean isAttached() {
        return (this.meta & 4) > 0;
    }

    public boolean isDisarmed() {
        return (this.meta & 8) > 0;
    }

    public void setPowered(boolean value) {
        if (value ^ this.isPowered()) {
            this.meta ^= 0x01;
        }
    }

    public void setAttached(boolean value) {
        if (value ^ this.isAttached()) {
            this.meta ^= 0x04;
        }
    }

    public void setDisarmed(boolean value) {
        if (value ^ this.isDisarmed()) {
            this.meta ^= 0x08;
        }
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.doesTriggerPressurePlate()) {
            return;
        }

        boolean powered = this.isPowered();

        if (!powered) {
            this.setPowered(true);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);

            this.level.scheduleUpdate(this, 10);
        }
    }

    public void updateHook(boolean scheduleUpdate) {
        for (BlockFace side : new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST}) {
            for (int i = 1; i < 42; ++i) {
                Block block = this.getSide(side, i);

                if (block instanceof BlockTripWireHook) {
                    BlockTripWireHook hook = (BlockTripWireHook) block;

                    if (hook.getFacing() == side.getOpposite()) {
                        hook.calculateState(false, true, i, this);
                    }

                    /*if(scheduleUpdate) {
                        this.level.scheduleUpdate(hook, 10);
                    }*/
                    break;
                }

                if (block.getId() != Block.TRIPWIRE) {
                    break;
                }
            }
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isPowered()) {
                return type;
            }

            boolean found = false;
            for (Entity entity : this.level.getCollidingEntities(this.getCollisionBoundingBox())) {
                if (!entity.doesTriggerPressurePlate()) {
                    continue;
                }

                found = true;
            }

            if (found) {
                this.level.scheduleUpdate(this, 10);
            } else {
                this.setPowered(false);
                this.level.setBlock(this, this, true, false);
                this.updateHook(false);
            }
            return type;
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(this, this, true, true);
        this.updateHook(false);

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (item.getId() == Item.SHEARS) {
            this.setDisarmed(true);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);
            this.getLevel().setBlock(this, new BlockAir(), true, true);
        } else {
            this.setPowered(true);
            this.getLevel().setBlock(this, new BlockAir(), true, true);
            this.updateHook(true);
        }

        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new AxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.5, this.z + 1);
    }
}
