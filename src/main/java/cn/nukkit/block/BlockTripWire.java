package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.TRIPWIRE;
import static cn.nukkit.item.ItemIds.SHEARS;

/**
 * @author CreeperFace
 */
public class BlockTripWire extends FloodableBlock {

    public BlockTripWire(Identifier id) {
        super(id);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.STRING);
    }

    public boolean isPowered() {
        return (this.getMeta() & 1) > 0;
    }

    public void setPowered(boolean value) {
        if (value ^ this.isPowered()) {
            this.setDamage(this.getMeta() ^ 0x01);
        }
    }

    public boolean isAttached() {
        return (this.getMeta() & 4) > 0;
    }

    public void setAttached(boolean value) {
        if (value ^ this.isAttached()) {
            this.setDamage(this.getMeta() ^ 0x04);
        }
    }

    public boolean isDisarmed() {
        return (this.getMeta() & 8) > 0;
    }

    public void setDisarmed(boolean value) {
        if (value ^ this.isDisarmed()) {
            this.setDamage(this.getMeta() ^ 0x08);
        }
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.canTriggerPressurePlate()) {
            return;
        }

        boolean powered = this.isPowered();

        if (!powered) {
            this.setPowered(true);
            this.level.setBlock(this.getPosition(), this, true, false);
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

                if (block.getId() != TRIPWIRE) {
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
                if (!entity.canTriggerPressurePlate()) {
                    continue;
                }

                found = true;
            }

            if (found) {
                this.level.scheduleUpdate(this, 10);
            } else {
                this.setPowered(false);
                this.level.setBlock(this.getPosition(), this, true, false);
                this.updateHook(false);
            }
            return type;
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.getLevel().setBlock(this.getPosition(), this, true, true);
        this.updateHook(false);

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (item.getId() == SHEARS) {
            this.setDisarmed(true);
            this.level.setBlock(this.getPosition(), this, true, false);
            this.updateHook(false);
            this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);
        } else {
            this.setPowered(true);
            this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true, true);
            this.updateHook(true);
        }

        return true;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.5f;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }
}
